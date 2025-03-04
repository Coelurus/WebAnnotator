import * as React from "react";
import * as ReactDOM from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider,
  useLocation,
  Outlet,
  Navigate
} from "react-router-dom";

import Root from "./routes/root";
import ErrorPage from "./error-page";
import Projects from "./routes/listing/projects";
import Users from "./routes/listing/users";
import Teams from "./routes/listing/teams";
import ProjectForm from "./routes/project/project-form";
import Project, {loader as projectLoader} from "./routes/project/project";
import LoginPage from "./routes/security/login/login-screen";
import SignupPage from "./routes/security/signup/signup-screen";
import LogoutButton from "./routes/security/logout/logout-screen";
import { isUserAdmin } from "./security/auth";
import HomePage from "./routes/home/home";

const AdminRoute = () => {
  const location = useLocation()
  return isUserAdmin()
    ? <Outlet />
    : <Navigate to = "/" replace state ={{ from: location }} />;
}

const router = createBrowserRouter([
  {
    path: "/home",
    element: <HomePage />
  },
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
        path: "admin",
        element: <AdminRoute />,
        children: [
          {
            path: "users",
            element: <Users />
          },
          {
            path: "teams",
            element: <Teams />
          },
        ]
      },
      {
        path: "login",
        element: <LoginPage />
      },
      {
        path: "signup",
        element: <SignupPage />
      },
      {
        path: "logout",
        element: <LogoutButton />
      }
    ]
  },
]);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
