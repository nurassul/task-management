import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useAuth } from "../../auth/AuthContext";

const ROLE_FILTERS = ["ALL", "ROLE_USER", "ROLE_ADMIN"];
const STATUS_FILTERS = ["ALL", "ACTIVE", "BANNED", "DELETED"];

function formatDate(value) {
  if (!value) {
    return "n/a";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString();
}

function AdminUsersPage() {
  const { authorizedRequest, currentUser } = useAuth();

  const [users, setUsers] = useState([]);
  const [searchValue, setSearchValue] = useState("");
  const [roleFilter, setRoleFilter] = useState("ALL");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [activeAction, setActiveAction] = useState("");

  const loadUsers = useCallback(async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const response = await authorizedRequest("/users");
      const list = Array.isArray(response) ? response : [];
      list.sort((first, second) => (first.id || 0) - (second.id || 0));
      setUsers(list);
    } catch (error) {
      setErrorMessage(error.message || "Failed to load users");
    } finally {
      setIsLoading(false);
    }
  }, [authorizedRequest]);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  const filteredUsers = useMemo(() => {
    const normalizedSearch = searchValue.trim().toLowerCase();

    return users.filter((user) => {
      const byRole = roleFilter === "ALL" || user.role === roleFilter;
      const byStatus = statusFilter === "ALL" || user.userStatus === statusFilter;

      if (!byRole || !byStatus) {
        return false;
      }

      if (!normalizedSearch) {
        return true;
      }

      return [user.id, user.username, user.email, user.role, user.userStatus]
        .filter((value) => value !== null && value !== undefined)
        .some((value) => String(value).toLowerCase().includes(normalizedSearch));
    });
  }, [users, roleFilter, searchValue, statusFilter]);

  const summary = useMemo(() => {
    return users.reduce(
      (acc, user) => {
        acc.total += 1;
        if (user.role === "ROLE_ADMIN") {
          acc.admins += 1;
        }
        if (user.role === "ROLE_USER") {
          acc.users += 1;
        }
        if (user.userStatus === "ACTIVE") {
          acc.active += 1;
        }
        if (user.userStatus === "BANNED") {
          acc.banned += 1;
        }
        if (user.userStatus === "DELETED") {
          acc.deleted += 1;
        }
        return acc;
      },
      { total: 0, admins: 0, users: 0, active: 0, banned: 0, deleted: 0 }
    );
  }, [users]);

  const runAdminAction = async (userId, actionName, requestFactory, successText) => {
    setErrorMessage("");
    setSuccessMessage("");
    setActiveAction(`${actionName}-${userId}`);

    try {
      await requestFactory();
      setSuccessMessage(successText);
      await loadUsers();
    } catch (error) {
      setErrorMessage(error.message || `Failed to ${actionName} user`);
    } finally {
      setActiveAction("");
    }
  };

  const handleBan = async (user) => {
    const confirmed = window.confirm(`Ban user ${user.username || user.email}?`);
    if (!confirmed) {
      return;
    }

    await runAdminAction(
      user.id,
      "ban",
      () =>
        authorizedRequest(`/users/${user.id}/banUser`, {
          method: "POST",
        }),
      `User #${user.id} has been banned.`
    );
  };

  const handleDelete = async (user) => {
    const confirmed = window.confirm(`Delete user ${user.username || user.email}?`);
    if (!confirmed) {
      return;
    }

    await runAdminAction(
      user.id,
      "delete",
      () =>
        authorizedRequest(`/users/${user.id}/delete`, {
          method: "DELETE",
        }),
      `User #${user.id} has been deleted.`
    );
  };

  return (
    <section className="admin-page">
      <div className="section-head">
        <div>
          <h2>Admin: Users</h2>
          <p className="section-hint">
            Protected endpoints: GET /users, POST /users/:id/banUser, DELETE /users/:id/delete.
          </p>
        </div>
        <button type="button" className="ghost-btn" onClick={loadUsers} disabled={isLoading}>
          {isLoading ? "Loading..." : "Refresh users"}
        </button>
      </div>

      {errorMessage ? <div className="alert error">{errorMessage}</div> : null}
      {successMessage ? <div className="alert success">{successMessage}</div> : null}

      <div className="stats-grid admin-stats-grid">
        <article className="card stat-card">
          <p>Total users</p>
          <strong>{summary.total}</strong>
        </article>
        <article className="card stat-card">
          <p>Admins</p>
          <strong>{summary.admins}</strong>
        </article>
        <article className="card stat-card">
          <p>Regular users</p>
          <strong>{summary.users}</strong>
        </article>
        <article className="card stat-card">
          <p>Active</p>
          <strong>{summary.active}</strong>
        </article>
        <article className="card stat-card">
          <p>Banned</p>
          <strong>{summary.banned}</strong>
        </article>
        <article className="card stat-card">
          <p>Deleted</p>
          <strong>{summary.deleted}</strong>
        </article>
      </div>

      <div className="toolbar card admin-toolbar">
        <input
          type="text"
          placeholder="Search by id, username, email, role, status..."
          value={searchValue}
          onChange={(event) => setSearchValue(event.target.value)}
        />

        <div className="admin-filters">
          <label>
            Role
            <select value={roleFilter} onChange={(event) => setRoleFilter(event.target.value)}>
              {ROLE_FILTERS.map((role) => (
                <option key={role} value={role}>
                  {role}
                </option>
              ))}
            </select>
          </label>

          <label>
            Status
            <select value={statusFilter} onChange={(event) => setStatusFilter(event.target.value)}>
              {STATUS_FILTERS.map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
          </label>
        </div>
      </div>

      <div className="table-wrap card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Registered</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers.length ? (
              filteredUsers.map((user) => {
                const isSelf = currentUser?.id === user.id;
                const isBanned = user.userStatus === "BANNED";
                const isDeleted = user.userStatus === "DELETED";

                return (
                  <tr key={user.id}>
                    <td>#{user.id}</td>
                    <td>{user.username || "-"}</td>
                    <td>{user.email || "-"}</td>
                    <td>{user.role || "-"}</td>
                    <td>{user.userStatus || "-"}</td>
                    <td>{formatDate(user.registrationDate)}</td>
                    <td>
                      <div className="admin-row-actions">
                        <button
                          type="button"
                          className="row-action"
                          disabled={
                            isSelf ||
                            isDeleted ||
                            isBanned ||
                            activeAction === `ban-${user.id}`
                          }
                          onClick={() => handleBan(user)}
                        >
                          {activeAction === `ban-${user.id}` ? "Banning..." : "Ban"}
                        </button>

                        <button
                          type="button"
                          className="row-action danger"
                          disabled={isSelf || isDeleted || activeAction === `delete-${user.id}`}
                          onClick={() => handleDelete(user)}
                        >
                          {activeAction === `delete-${user.id}` ? "Deleting..." : "Delete"}
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })
            ) : (
              <tr>
                <td colSpan={7}>
                  <div className="empty-state">No users found for current filters.</div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

export default AdminUsersPage;
