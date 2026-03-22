import React, { useCallback, useEffect, useMemo, useState } from "react";
import { apiRequest } from "../api/apiClient";
import { useAuth } from "../auth/AuthContext";

function StatisticsPage() {
  const { currentUser, fetchCurrentUser } = useAuth();

  const [taskStats, setTaskStats] = useState(null);
  const [userStats, setUserStats] = useState(null);
  const [userIdInput, setUserIdInput] = useState("");

  const [taskLoading, setTaskLoading] = useState(true);
  const [userLoading, setUserLoading] = useState(false);

  const [taskError, setTaskError] = useState("");
  const [userError, setUserError] = useState("");

  const loadTaskStats = useCallback(async () => {
    setTaskLoading(true);
    setTaskError("");

    try {
      const data = await apiRequest("/stats/task");
      setTaskStats(data);
    } catch (error) {
      setTaskError(error.message || "Failed to load global task statistics");
    } finally {
      setTaskLoading(false);
    }
  }, []);

  const loadUserStats = useCallback(async (userId) => {
    if (!userId) {
      setUserError("User id is required");
      return;
    }

    setUserLoading(true);
    setUserError("");

    try {
      const data = await apiRequest(`/stats/user/${userId}`);
      setUserStats(data);
    } catch (error) {
      setUserStats(null);
      if (String(error.message || "").includes("500")) {
        setUserError("User statistics are not initialized yet. Create/update a task or refresh in a few seconds.");
      } else {
        setUserError(error.message || "Failed to load user statistics");
      }
    } finally {
      setUserLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTaskStats();
  }, [loadTaskStats]);

  useEffect(() => {
    let active = true;

    const bootstrapUserStats = async () => {
      try {
        const user = currentUser?.id ? currentUser : await fetchCurrentUser();
        if (!active || !user?.id) {
          return;
        }

        setUserIdInput(String(user.id));
        await loadUserStats(user.id);
      } catch (error) {
        if (active) {
          setUserError(error.message || "Failed to resolve current user");
        }
      }
    };

    bootstrapUserStats();

    return () => {
      active = false;
    };
  }, [currentUser, fetchCurrentUser, loadUserStats]);

  const globalCards = useMemo(() => {
    if (!taskStats) {
      return [];
    }

    return [
      { label: "Created", value: taskStats.totalCreated },
      { label: "In progress", value: taskStats.totalInProgress },
      { label: "Done", value: taskStats.totalDone },
      { label: "Priority LOW", value: taskStats.lowPriorityCount },
      { label: "Priority MEDIUM", value: taskStats.mediumPriorityCount },
      { label: "Priority HIGH", value: taskStats.highPriorityCount },
    ];
  }, [taskStats]);

  const userCards = useMemo(() => {
    if (!userStats) {
      return [];
    }

    return [
      { label: "Total created", value: userStats.totalCreated },
      { label: "Total assigned", value: userStats.totalAssigned },
      { label: "Todo", value: userStats.todoCount },
      { label: "In progress", value: userStats.inProgressCount },
      { label: "Done", value: userStats.doneCount },
      { label: "LOW", value: userStats.lowPriorityCount },
      { label: "MEDIUM", value: userStats.mediumPriorityCount },
      { label: "HIGH", value: userStats.highPriorityCount },
    ];
  }, [userStats]);

  const handleUserSubmit = async (event) => {
    event.preventDefault();
    await loadUserStats(userIdInput.trim());
  };

  return (
    <section className="statistics-page">
      <div className="section-head">
        <div>
          <h2>Statistics</h2>
          <p className="section-hint">Endpoints: GET /stats/task and GET /stats/user/:userId.</p>
        </div>
        <button type="button" className="ghost-btn" onClick={loadTaskStats} disabled={taskLoading}>
          {taskLoading ? "Loading..." : "Refresh global"}
        </button>
      </div>

      <div className="stats-two-col">
        <article className="card">
          <h3>Global task stats</h3>
          {taskError ? <div className="alert error">{taskError}</div> : null}
          {taskLoading && !taskStats ? <p className="muted">Loading task stats...</p> : null}
          <div className="mini-grid">
            {globalCards.map((item) => (
              <div key={item.label} className="mini-stat">
                <span>{item.label}</span>
                <strong>{item.value ?? 0}</strong>
              </div>
            ))}
          </div>
        </article>

        <article className="card">
          <h3>User stats</h3>

          <form className="user-stats-form" onSubmit={handleUserSubmit}>
            <label htmlFor="user-id">User ID</label>
            <input
              id="user-id"
              type="number"
              min="1"
              value={userIdInput}
              onChange={(event) => setUserIdInput(event.target.value)}
              placeholder="Enter user id"
            />
            <button type="submit" className="primary-btn" disabled={userLoading}>
              {userLoading ? "Loading..." : "Load user stats"}
            </button>
          </form>

          {userError ? <div className="alert error">{userError}</div> : null}
          <div className="mini-grid">
            {userCards.map((item) => (
              <div key={item.label} className="mini-stat">
                <span>{item.label}</span>
                <strong>{item.value ?? 0}</strong>
              </div>
            ))}
          </div>
        </article>
      </div>
    </section>
  );
}

export default StatisticsPage;
