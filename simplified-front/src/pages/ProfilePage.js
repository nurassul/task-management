import React, { useEffect, useMemo, useState } from "react";
import { useAuth } from "../auth/AuthContext";

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

function ProfilePage() {
  const { currentUser, fetchCurrentUser, updateCurrentUser } = useAuth();

  const [form, setForm] = useState({
    username: "",
    email: "",
  });
  const [isLoading, setIsLoading] = useState(!currentUser);
  const [isSaving, setIsSaving] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    if (currentUser) {
      setForm({
        username: currentUser.username || "",
        email: currentUser.email || "",
      });
      setIsLoading(false);
      return;
    }

    let isMounted = true;
    setIsLoading(true);
    fetchCurrentUser()
      .catch((error) => {
        if (isMounted) {
          setErrorMessage(error.message || "Failed to load user profile");
        }
      })
      .finally(() => {
        if (isMounted) {
          setIsLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [currentUser, fetchCurrentUser]);

  const userMeta = useMemo(() => {
    if (!currentUser) {
      return [];
    }

    return [
      { label: "User ID", value: currentUser.id ?? "n/a" },
      { label: "Role", value: currentUser.role || "ROLE_USER" },
      { label: "Status", value: currentUser.userStatus || "ACTIVE" },
      { label: "Registered", value: formatDate(currentUser.registrationDate) },
    ];
  }, [currentUser]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage("");
    setSuccessMessage("");

    if (!currentUser?.id) {
      setErrorMessage("Unable to update profile: user id is missing");
      return;
    }

    setIsSaving(true);
    try {
      await updateCurrentUser({
        id: currentUser.id,
        username: form.username.trim(),
        email: form.email.trim(),
      });
      setSuccessMessage("Profile updated");
    } catch (error) {
      setErrorMessage(error.message || "Failed to update profile");
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return <div className="card muted">Loading profile...</div>;
  }

  return (
    <section className="profile-layout">
      <div className="card profile-meta">
        <h2>Profile</h2>
        <p className="section-hint">Endpoints: GET /users/email/:email and PUT /users/:id.</p>

        <dl className="meta-grid">
          {userMeta.map((item) => (
            <div key={item.label}>
              <dt>{item.label}</dt>
              <dd>{item.value}</dd>
            </div>
          ))}
        </dl>
      </div>

      <form className="card profile-form" onSubmit={handleSubmit}>
        <h3>Edit profile</h3>
        {errorMessage ? <div className="alert error">{errorMessage}</div> : null}
        {successMessage ? <div className="alert success">{successMessage}</div> : null}

        <label htmlFor="username">Username</label>
        <input
          id="username"
          name="username"
          type="text"
          value={form.username}
          onChange={handleChange}
          required
        />

        <label htmlFor="email">Email</label>
        <input
          id="email"
          name="email"
          type="email"
          value={form.email}
          onChange={handleChange}
          required
        />

        <button type="submit" className="primary-btn" disabled={isSaving}>
          {isSaving ? "Saving..." : "Save changes"}
        </button>
      </form>
    </section>
  );
}

export default ProfilePage;
