import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

function RegistrationPage() {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage("");

    if (form.password !== form.confirmPassword) {
      setErrorMessage("Passwords do not match");
      return;
    }

    setIsSubmitting(true);
    try {
      await register({
        username: form.username.trim(),
        email: form.email.trim(),
        password: form.password,
      });

      navigate("/login", {
        replace: true,
        state: { registrationSuccess: true, email: form.email.trim() },
      });
    } catch (error) {
      setErrorMessage(error.message || "Registration failed");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div>
      <h2>Create account</h2>
      <p className="auth-subtitle">Uses `/users/registration` endpoint.</p>

      {errorMessage ? <div className="alert error">{errorMessage}</div> : null}

      <form className="form-card" onSubmit={handleSubmit}>
        <label htmlFor="username">Username</label>
        <input
          id="username"
          name="username"
          type="text"
          autoComplete="name"
          placeholder="Your name"
          value={form.username}
          onChange={handleChange}
          required
        />

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
          autoComplete="new-password"
          placeholder="At least 1 secure password"
          value={form.password}
          onChange={handleChange}
          required
        />

        <label htmlFor="confirmPassword">Confirm password</label>
        <input
          id="confirmPassword"
          name="confirmPassword"
          type="password"
          autoComplete="new-password"
          placeholder="Repeat password"
          value={form.confirmPassword}
          onChange={handleChange}
          required
        />

        <button className="primary-btn" type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Creating account..." : "Register"}
        </button>
      </form>

      <p className="auth-footnote">
        Already have an account? <Link to="/login">Sign in</Link>
      </p>
    </div>
  );
}

export default RegistrationPage;
