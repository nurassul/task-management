import React from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

function AppLayout() {
  const navigate = useNavigate();
  const { currentUser, logout, isAdmin } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <p className="brand-kicker">Task Management</p>
          <h1 className="brand-title">Operations Panel</h1>
        </div>

        <nav className="main-nav">
          <NavLink to="/dashboard" className={({ isActive }) => (isActive ? "active" : "")}>
            Dashboard
          </NavLink>
          <NavLink to="/tasks/new" className={({ isActive }) => (isActive ? "active" : "")}>
            Create Task
          </NavLink>
          <NavLink to="/statistics" className={({ isActive }) => (isActive ? "active" : "")}>
            Statistics
          </NavLink>
          <NavLink to="/profile" className={({ isActive }) => (isActive ? "active" : "")}>
            Profile
          </NavLink>
          {isAdmin ? (
            <NavLink to="/admin/users" className={({ isActive }) => (isActive ? "active" : "")}>
              Admin
            </NavLink>
          ) : null}
        </nav>

        <div className="topbar-meta">
          <p>{currentUser?.username || currentUser?.email || "Signed in"}</p>
          <button className="ghost-btn" type="button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="page-body">
        <Outlet />
      </main>
    </div>
  );
}

export default AppLayout;
