import React from "react";
import { Outlet } from "react-router-dom";

function AuthLayout() {
  return (
    <div className="auth-shell">
      <section className="auth-panel">
        <div className="auth-brand">
          <p>Gateway connected</p>
          <h1>Task Control Center</h1>
          <span className="auth-pill">Microservices: users + tasks + stats</span>
        </div>
        <Outlet />
      </section>
    </div>
  );
}

export default AuthLayout;
