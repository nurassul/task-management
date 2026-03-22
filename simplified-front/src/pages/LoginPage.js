import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, fetchCurrentUser } = useAuth();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [infoMessage, setInfoMessage] = useState("");

  const redirectPath = location.state?.from?.pathname || "/dashboard";

  useEffect(() => {
    if (location.state?.registrationSuccess) {
      setInfoMessage("Registration completed. Now sign in.");
      setForm((prev) => ({
        ...prev,
        email: location.state.email || prev.email,
      }));
    }
  }, [location.state]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsSubmitting(true);
    setErrorMessage("");

    try {
      await login(form);
      await fetchCurrentUser();
      navigate(redirectPath, { replace: true });
    } catch (error) {
      setErrorMessage(error.message || "Login failed");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div>
      <h2>Sign in</h2>
      <p className="auth-subtitle">Use `/auth/sign-in` through API Gateway.</p>

      {infoMessage ? <div className="alert success">{infoMessage}</div> : null}
      {errorMessage ? <div className="alert error">{errorMessage}</div> : null}

      <form className="form-card" onSubmit={handleSubmit}>
        <label htmlFor="email">Email</label>
        <input
          id="email"
          name="email"
          type="email"
          autoComplete="email"
          placeholder="you@example.com"
          value={form.email}
          onChange={handleChange}
          required
        />

        <label htmlFor="password">Password</label>
        <input
          id="password"
          name="password"
          type="password"
          autoComplete="current-password"
          placeholder="Enter password"
          value={form.password}
          onChange={handleChange}
          required
        />

        <button className="primary-btn" type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Signing in..." : "Sign in"}
        </button>
      </form>

      <p className="auth-footnote">
        No account yet? <Link to="/register">Create one</Link>
      </p>
    </div>
  );
}

export default LoginPage;
