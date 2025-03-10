import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import {
  createBrowserRouter,
  RouterProvider,
  useLocation,
  Outlet,
  Navigate
} from 'react-router-dom';

import ErrorPage from './error-page';
import Projects from './screens/listing/projects/projects';
import Users from './screens/listing/users/users';
import Teams from './screens/listing/teams/teams';
import Project, { loader as projectLoader } from './screens/editor/annotator';
import LoginPage from './screens/security/login/login-screen';
import SignupPage from './screens/security/signup/signup-screen';
import { isUserAdmin, isUserLoggedIn } from './security/auth';
import HomePage from './screens/root/menu';

const AdminRoute = () => {
  const location = useLocation();
  return isUserAdmin() ? <Outlet /> : <Navigate to="/" replace state={{ from: location }} />;
};

const SignedUserRoute = () => {
  const location = useLocation();
  return isUserLoggedIn() ? <Outlet /> : <Navigate to="/" replace state={{ from: location }} />;
};

const AnonymousUserRoute = () => {
  const location = useLocation();
  return !isUserLoggedIn() ? <Outlet /> : <Navigate to="/" replace state={{ from: location }} />;
};

const router = createBrowserRouter([
  {
    path: '/',
    element: <HomePage />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: 'user',
        element: <AnonymousUserRoute />,
        children: [
          {
            path: 'login',
            element: <LoginPage />
          },
          {
            path: 'signup',
            element: <SignupPage />
          }
        ]
      },
      {
        path: 'admin',
        element: <AdminRoute />,
        children: [
          {
            path: 'users',
            element: <Users />
          },
          {
            path: 'teams',
            element: <Teams />
          }
        ]
      },
      {
        path: 'projects',
        element: <SignedUserRoute />,
        children: [
          {
            path: 'all',
            element: <Projects />
          }
        ]
      }
    ]
  },
  {
    path: 'editor',
    element: <SignedUserRoute />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: ':projectId',
        element: <Project />,
        loader: projectLoader
      }
    ]
  }
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
