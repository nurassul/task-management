import { render, screen } from "@testing-library/react";
import App from "./App";

test("renders login page by default", async () => {
  render(<App />);
  expect(await screen.findByText(/sign in/i)).toBeInTheDocument();
});
