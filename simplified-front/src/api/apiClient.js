const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080";

export function buildApiUrl(path) {
  return `${API_BASE_URL}${path}`;
}

export async function parseApiResponse(response) {
  const bodyText = await response.text();

  let payload = null;
  if (bodyText) {
    try {
      payload = JSON.parse(bodyText);
    } catch {
      payload = bodyText;
    }
  }

  if (!response.ok) {
    const messageFromPayload =
      payload && typeof payload === "object"
        ? payload.details || payload.message
        : null;

    throw new Error(messageFromPayload || `Request failed with status ${response.status}`);
  }

  return payload;
}

export async function apiRequest(path, options = {}) {
  const response = await fetch(buildApiUrl(path), options);
  return parseApiResponse(response);
}
