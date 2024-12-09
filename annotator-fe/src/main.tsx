import * as React from "react";
import * as ReactDOM from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";

import Root from "./routes/root";
import ErrorPage from "./error-page";
import Projects from "./routes/listing/projects";
import Users from "./routes/listing/users";
import Teams from "./routes/listing/teams";
import ProjectForm from "./routes/project/project-form";
import Project, {loader as projectLoader} from "./routes/project/project";
import LoginPage from "./routes/security/log-in";
import SignupPage from "./routes/security/sign-up";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Root />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: "projects",
        children: [
          {
            path: ":projectId",
            element: <Project />,
            loader: projectLoader
          },
          {
            path: "all",
            element: <Projects />,
          },
          {
            path: "create",
            element: <ProjectForm />,
          },
        ]
      },
      {
        path: "users",
        element: <Users />
      },
      {
        path: "teams",
        element: <Teams />
      },
      {
        path: "login",
        element: <LoginPage />
      },
      {
        path: "signup",
        element: <SignupPage />
      }
    ]
  },
]);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
