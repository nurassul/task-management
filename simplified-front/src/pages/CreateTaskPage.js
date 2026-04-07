import React, { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { apiRequest } from "../api/apiClient";
import { useAuth } from "../auth/AuthContext";

const PRIORITIES = ["LOW", "MEDIUM", "HIGH"];

function getTomorrowDateString() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return tomorrow.toISOString().slice(0, 10);
}

function CreateTaskPage() {
  const { currentUser, fetchCurrentUser } = useAuth();
  const [creatorId, setCreatorId] = useState(currentUser?.id ? String(currentUser.id) : "");
  const [form, setForm] = useState({
    descriptionOfTask: "",
    assignedUserId: "",
    deadlineDate: getTomorrowDateString(),
    priority: "MEDIUM",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    if (currentUser?.id) {
      setCreatorId(String(currentUser.id));
      return;
    }

    let isMounted = true;
    fetchCurrentUser()
      .then((user) => {
        if (isMounted && user?.id) {
          setCreatorId(String(user.id));
        }
      })
      .catch(() => {
        if (isMounted) {
          setErrorMessage("Could not detect creator id from current profile.");
        }
      });

    return () => {
      isMounted = false;
    };
  }, [currentUser, fetchCurrentUser]);

  const minDeadline = useMemo(() => getTomorrowDateString(), []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const validateBeforeSubmit = () => {
    if (!creatorId) {
      return "Creator id is required.";
    }

    if (!form.deadlineDate) {
      return "Deadline is required.";
    }

    if (form.deadlineDate <= new Date().toISOString().slice(0, 10)) {
      return "Deadline must be in the future.";
    }

    if (!form.priority) {
      return "Priority is required.";
    }

    if (form.assignedUserId && Number(form.assignedUserId) <= 0) {
      return "Assigned user id must be greater than 0.";
    }

    return null;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage("");
    setSuccessMessage("");

    const validationError = validateBeforeSubmit();
    if (validationError) {
      setErrorMessage(validationError);
      return;
    }

    const payload = {
      creatorId: Number(creatorId),
      descriptionOfTask: form.descriptionOfTask.trim() || null,
      assignedUserId: form.assignedUserId ? Number(form.assignedUserId) : null,
      deadlineDate: form.deadlineDate,
      priority: form.priority,
    };

    setIsSubmitting(true);
    try {
      const createdTask = await apiRequest("/tasks", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      setSuccessMessage(`Task #${createdTask?.id ?? "new"} created successfully.`);
      setForm((prev) => ({
        ...prev,
        descriptionOfTask: "",
        assignedUserId: "",
        deadlineDate: getTomorrowDateString(),
      }));
    } catch (error) {
      setErrorMessage(error.message || "Failed to create task");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <section className="create-task-page">
      <div className="section-head">
        <div>
          <h2>Create task</h2>
          <p className="section-hint">Endpoint: POST /tasks via API Gateway.</p>
        </div>
        <Link to="/dashboard" className="ghost-btn inline-link-btn">
          Back to dashboard
        </Link>
      </div>

      <div className="card create-task-card">
        <form className="task-form-grid" onSubmit={handleSubmit}>
          {errorMessage ? <div className="alert error">{errorMessage}</div> : null}
          {successMessage ? <div className="alert success">{successMessage}</div> : null}

          <div className="task-form-row">
            <label htmlFor="creatorId">Creator ID</label>
            <input
              id="creatorId"
              type="number"
              min="1"
              value={creatorId}
              onChange={(event) => setCreatorId(event.target.value)}
              required
            />
          </div>

          <div className="task-form-row">
            <label htmlFor="assignedUserId">Assigned user ID</label>
            <input
              id="assignedUserId"
              name="assignedUserId"
              type="number"
              min="1"
              placeholder="Mandatory"
              value={form.assignedUserId}
              onChange={handleChange}
            />
          </div>

          <div className="task-form-row">
            <label htmlFor="deadlineDate">Deadline date</label>
            <input
              id="deadlineDate"
              name="deadlineDate"
              type="date"
              min={minDeadline}
              value={form.deadlineDate}
              onChange={handleChange}
              required
            />
          </div>

          <div className="task-form-row">
            <label htmlFor="priority">Priority</label>
            <select id="priority" name="priority" value={form.priority} onChange={handleChange} required>
              {PRIORITIES.map((priority) => (
                <option key={priority} value={priority}>
                  {priority}
                </option>
              ))}
            </select>
          </div>

          <div className="task-form-row wide">
            <label htmlFor="descriptionOfTask">Task description</label>
            <textarea
              id="descriptionOfTask"
              name="descriptionOfTask"
              rows={5}
              placeholder="Describe what should be done..."
              value={form.descriptionOfTask}
              onChange={handleChange}
            />
          </div>

          <div className="task-form-actions">
            <button className="primary-btn" type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Creating..." : "Create task"}
            </button>
            <Link to="/statistics" className="ghost-btn inline-link-btn">
              Open statistics
            </Link>
          </div>
        </form>
      </div>
    </section>
  );
}

export default CreateTaskPage;
