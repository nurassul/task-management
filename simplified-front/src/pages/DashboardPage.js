import React, { useCallback, useEffect, useMemo, useState } from "react";
import { apiRequest } from "../api/apiClient";
import { useAuth } from "../auth/AuthContext";
import { Link } from "react-router-dom";

const STATUS_FILTERS = ["ALL", "CREATED", "IN_PROGRESS", "DONE"];

const EMPTY_TASK_STATS = {
  totalCreated: 0,
  totalInProgress: 0,
  totalDone: 0,
  lowPriorityCount: 0,
  mediumPriorityCount: 0,
  highPriorityCount: 0,
};

function formatDate(value) {
  if (!value) {
    return "n/a";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleDateString();
}

function DashboardPage() {
  const { currentUser, fetchCurrentUser } = useAuth();

  const [tasks, setTasks] = useState([]);
  const [taskStats, setTaskStats] = useState(EMPTY_TASK_STATS);
  const [userStats, setUserStats] = useState(null);
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [searchValue, setSearchValue] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isStatsLoading, setIsStatsLoading] = useState(true);
  const [taskErrorMessage, setTaskErrorMessage] = useState("");
  const [statsErrorMessage, setStatsErrorMessage] = useState("");
  const [userStatsMessage, setUserStatsMessage] = useState("");
  const [activeTaskAction, setActiveTaskAction] = useState(null);

  const loadTasks = useCallback(async () => {
    setIsLoading(true);
    setTaskErrorMessage("");

    try {
      const data = await apiRequest("/tasks");
      const list = Array.isArray(data) ? data : [];
      list.sort((first, second) => (second.id || 0) - (first.id || 0));
      setTasks(list);
    } catch (error) {
      setTaskErrorMessage(error.message || "Failed to load tasks");
    } finally {
      setIsLoading(false);
    }
  }, []);

  const loadStats = useCallback(async () => {
    setIsStatsLoading(true);
    setStatsErrorMessage("");
    setUserStatsMessage("");

    try {
      const taskStatsData = await apiRequest("/stats/task");
      setTaskStats(taskStatsData || EMPTY_TASK_STATS);
    } catch (error) {
      setTaskStats(EMPTY_TASK_STATS);
      setStatsErrorMessage(error.message || "Failed to load global statistics");
    }

    let userId = currentUser?.id || null;
    if (!userId) {
      try {
        const user = await fetchCurrentUser();
        userId = user?.id || null;
      } catch {
        userId = null;
      }
    }

    if (!userId) {
      setUserStats(null);
      setUserStatsMessage("Current user id is not available yet.");
      setIsStatsLoading(false);
      return;
    }

    try {
      const userStatsData = await apiRequest(`/stats/user/${userId}`);
      setUserStats(userStatsData);
    } catch {
      setUserStats(null);
      setUserStatsMessage("User statistics are not ready yet (service returned 500).");
    } finally {
      setIsStatsLoading(false);
    }
  }, [currentUser, fetchCurrentUser]);

  const refreshDashboard = useCallback(async () => {
    await Promise.all([loadTasks(), loadStats()]);
  }, [loadTasks, loadStats]);

  useEffect(() => {
    refreshDashboard();
  }, [refreshDashboard]);

  const filteredTasks = useMemo(() => {
    const normalizedSearch = searchValue.trim().toLowerCase();

    return tasks.filter((task) => {
      const byStatus = statusFilter === "ALL" || task.taskStatus === statusFilter;
      if (!byStatus) {
        return false;
      }

      if (!normalizedSearch) {
        return true;
      }

      return [
        task.id,
        task.creatorId,
        task.assignedUserId,
        task.descriptionOfTask,
        task.priority,
        task.taskStatus,
      ]
        .filter((value) => value !== null && value !== undefined)
        .some((value) => String(value).toLowerCase().includes(normalizedSearch));
    });
  }, [tasks, statusFilter, searchValue]);

  const updateTaskStatus = async (taskId, action) => {
    const endpoint = action === "start" ? `/tasks/${taskId}/start` : `/tasks/${taskId}/complete`;
    setActiveTaskAction(`${taskId}-${action}`);
    setTaskErrorMessage("");

    try {
      await apiRequest(endpoint, {
        method: "POST",
      });
      await refreshDashboard();
    } catch (error) {
      setTaskErrorMessage(error.message || "Failed to update task");
    } finally {
      setActiveTaskAction(null);
    }
  };

  return (
    <section>
      <div className="section-head">
        <div>
          <h2>Dashboard</h2>
          <p className="section-hint">
            Statistics from GET /stats/task and GET /stats/user/:userId, tasks list from GET /tasks.
          </p>
        </div>
        <div className="section-head-actions">
          <Link to="/tasks/new" className="primary-btn inline-link-btn">
            + New task
          </Link>
          <button
            type="button"
            className="ghost-btn"
            onClick={refreshDashboard}
            disabled={isLoading || isStatsLoading}
          >
            {isLoading || isStatsLoading ? "Loading..." : "Refresh"}
          </button>
        </div>
      </div>

      {statsErrorMessage ? <div className="alert error">{statsErrorMessage}</div> : null}
      {taskErrorMessage ? <div className="alert error">{taskErrorMessage}</div> : null}

      <div className="stats-grid">
        <article className="card stat-card">
          <p>All tasks</p>
          <strong>{taskStats.totalCreated}</strong>
        </article>
        <article className="card stat-card">
          <p>In progress</p>
          <strong>{taskStats.totalInProgress}</strong>
        </article>
        <article className="card stat-card">
          <p>Done</p>
          <strong>{taskStats.totalDone}</strong>
        </article>
        <article className="card stat-card">
          <p>Low priority</p>
          <strong>{taskStats.lowPriorityCount}</strong>
        </article>
        <article className="card stat-card">
          <p>Medium priority</p>
          <strong>{taskStats.mediumPriorityCount}</strong>
        </article>
        <article className="card stat-card">
          <p>High priority</p>
          <strong>{taskStats.highPriorityCount}</strong>
        </article>
      </div>

      {userStats ? (
        <div className="card dashboard-user-card">
          <h3>My task statistics (user #{userStats.userId})</h3>
          <div className="mini-grid">
            <div className="mini-stat">
              <span>Created</span>
              <strong>{userStats.totalCreated}</strong>
            </div>
            <div className="mini-stat">
              <span>Assigned</span>
              <strong>{userStats.totalAssigned}</strong>
            </div>
            <div className="mini-stat">
              <span>Todo</span>
              <strong>{userStats.todoCount}</strong>
            </div>
            <div className="mini-stat">
              <span>In progress</span>
              <strong>{userStats.inProgressCount}</strong>
            </div>
            <div className="mini-stat">
              <span>Done</span>
              <strong>{userStats.doneCount}</strong>
            </div>
            <div className="mini-stat">
              <span>High priority</span>
              <strong>{userStats.highPriorityCount}</strong>
            </div>
          </div>
        </div>
      ) : null}

      {userStatsMessage ? <div className="alert warning">{userStatsMessage}</div> : null}

      <div className="toolbar card">
        <input
          type="text"
          placeholder="Search by id, description, user id, status..."
          value={searchValue}
          onChange={(event) => setSearchValue(event.target.value)}
        />

        <div className="chip-group">
          {STATUS_FILTERS.map((status) => (
            <button
              key={status}
              type="button"
              className={statusFilter === status ? "chip active" : "chip"}
              onClick={() => setStatusFilter(status)}
            >
              {status}
            </button>
          ))}
        </div>
      </div>

      <div className="table-wrap card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Description</th>
              <th>Creator</th>
              <th>Assignee</th>
              <th>Status</th>
              <th>Priority</th>
              <th>Deadline</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredTasks.length ? (
              filteredTasks.map((task) => (
                <tr key={task.id}>
                  <td>#{task.id}</td>
                  <td>{task.descriptionOfTask || "-"}</td>
                  <td>{task.creatorId || "-"}</td>
                  <td>{task.assignedUserId || "-"}</td>
                  <td>
                    <span className={`status-pill ${String(task.taskStatus || "").toLowerCase()}`}>
                      {task.taskStatus || "-"}
                    </span>
                  </td>
                  <td>{task.priority || "-"}</td>
                  <td>{formatDate(task.deadlineDate)}</td>
                  <td>
                    {task.taskStatus === "CREATED" ? (
                      <button
                        type="button"
                        className="row-action"
                        disabled={activeTaskAction === `${task.id}-start`}
                        onClick={() => updateTaskStatus(task.id, "start")}
                      >
                        Start
                      </button>
                    ) : null}
                    {task.taskStatus === "IN_PROGRESS" ? (
                      <button
                        type="button"
                        className="row-action"
                        disabled={activeTaskAction === `${task.id}-complete`}
                        onClick={() => updateTaskStatus(task.id, "complete")}
                      >
                        Complete
                      </button>
                    ) : null}
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={8}>
                  <div className="empty-state">No tasks found for current filters.</div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

export default DashboardPage;
