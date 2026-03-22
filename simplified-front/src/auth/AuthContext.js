import React, { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { apiRequest, buildApiUrl, parseApiResponse } from "../api/apiClient";

const STORAGE_KEY = "task_management_auth";

const AuthContext = createContext(null);

function decodeJwtPayload(token) {
  try {
    const payloadPart = token.split(".")[1];
    if (!payloadPart) {
      return null;
    }

    const normalized = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, "=");
    return JSON.parse(atob(padded));
  } catch {
    return null;
  }
}

function readPersistedAuthState() {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (!stored) {
      return null;
    }

    const parsed = JSON.parse(stored);
    if (!parsed?.token || !parsed?.refreshToken) {
      return null;
    }

    return {
      token: parsed.token,
      refreshToken: parsed.refreshToken,
      user: parsed.user || null,
    };
  } catch {
    return null;
  }
}

function getInitialAuthState() {
  return (
    readPersistedAuthState() || {
      token: null,
      refreshToken: null,
      user: null,
    }
  );
}

function withJsonHeaders(options = {}) {
  const headers = { ...(options.headers || {}) };
  if (options.body && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json";
  }
  return { ...options, headers };
}

export function AuthProvider({ children }) {
  const [authState, setAuthState] = useState(getInitialAuthState);
  const authRef = useRef(authState);

  useEffect(() => {
    authRef.current = authState;
    if (authState.token && authState.refreshToken) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(authState));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [authState]);

  const logout = useCallback(() => {
    const nextState = {
      token: null,
      refreshToken: null,
      user: null,
    };

    authRef.current = nextState;
    setAuthState(nextState);
  }, []);

  const refreshAccessToken = useCallback(async () => {
    const current = authRef.current;

    if (!current.refreshToken) {
      return null;
    }

    try {
      const refreshed = await apiRequest("/auth/refresh", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          refreshToken: current.refreshToken,
        }),
      });

      if (!refreshed?.token) {
        throw new Error("No access token returned from refresh endpoint");
      }

      const nextState = {
        ...authRef.current,
        token: refreshed.token,
        refreshToken: refreshed.refreshToken || authRef.current.refreshToken,
      };

      authRef.current = nextState;
      setAuthState((prev) => ({
        ...prev,
        token: nextState.token,
        refreshToken: nextState.refreshToken,
      }));

      return refreshed.token;
    } catch {
      logout();
      return null;
    }
  }, [logout]);

  const authorizedRequest = useCallback(
    async (path, options = {}, hasRetried = false) => {
      const current = authRef.current;
      if (!current.token) {
        throw new Error("Authentication required");
      }

      const preparedOptions = withJsonHeaders(options);
      const headers = {
        ...(preparedOptions.headers || {}),
        Authorization: `Bearer ${current.token}`,
      };

      const response = await fetch(buildApiUrl(path), {
        ...preparedOptions,
        headers,
      });

      if (response.status === 401 && !hasRetried && current.refreshToken) {
        const renewedToken = await refreshAccessToken();
        if (renewedToken) {
          return authorizedRequest(path, options, true);
        }
      }

      return parseApiResponse(response);
    },
    [refreshAccessToken]
  );

  const register = useCallback(async ({ username, email, password }) => {
    return apiRequest("/users/registration", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username,
        email,
        password,
      }),
    });
  }, []);

  const login = useCallback(async ({ email, password }) => {
    const tokens = await apiRequest("/auth/sign-in", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email,
        password,
      }),
    });

    const nextState = {
      token: tokens.token,
      refreshToken: tokens.refreshToken,
      user: null,
    };

    authRef.current = nextState;
    setAuthState(nextState);

    return tokens;
  }, []);

  const fetchCurrentUser = useCallback(async () => {
    const current = authRef.current;
    if (!current.token) {
      throw new Error("Authentication required");
    }

    const payload = decodeJwtPayload(current.token);
    const email = payload?.sub;
    if (!email) {
      throw new Error("Cannot detect email from access token");
    }

    const user = await authorizedRequest(`/users/email/${encodeURIComponent(email)}`);

    setAuthState((prev) => ({
      ...prev,
      user,
    }));

    return user;
  }, [authorizedRequest]);

  const updateCurrentUser = useCallback(
    async ({ id, username, email }) => {
      const user = await authorizedRequest(`/users/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username,
          email,
        }),
      });

      setAuthState((prev) => ({
        ...prev,
        user,
      }));

      return user;
    },
    [authorizedRequest]
  );

  const value = useMemo(
    () => {
      const tokenPayload = authState.token ? decodeJwtPayload(authState.token) : null;
      const normalizedRole = String(authState.user?.role || tokenPayload?.role || "").toUpperCase();
      const isAdmin = normalizedRole === "ROLE_ADMIN" || normalizedRole === "ADMIN";

      return {
        token: authState.token,
        refreshToken: authState.refreshToken,
        currentUser: authState.user,
        role: normalizedRole || null,
        isAdmin,
        isAuthenticated: Boolean(authState.token),
        login,
        logout,
        register,
        fetchCurrentUser,
        updateCurrentUser,
        authorizedRequest,
      };
    },
    [authState, login, logout, register, fetchCurrentUser, updateCurrentUser, authorizedRequest]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
}
