import React from 'react';
import ReactDOM from 'react-dom/client';
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

/**
 * AdminRoute component that checks if the user is an admin.
 * If the user is an admin, it renders the Outlet; otherwise, it redirects to the home page.
 * 
 * @returns JSX element representing the admin route.
 */
const AdminRoute = () => {
  const location = useLocation();
  return isUserAdmin() ? <Outlet /> : <Navigate to="/" replace state={{ from: location }} />;
};

/**
 * SignedUserRoute component that checks if the user is logged in.
 * If the user is logged in, it renders the Outlet; otherwise, it redirects to the home page.
 * 
 * @returns JSX element representing the signed user route.
 */
const SignedUserRoute = () => {
  const location = useLocation();
  return isUserLoggedIn() ? <Outlet /> : <Navigate to="/" replace state={{ from: location }} />;
};

/**
 * AnonymousUserRoute component that checks if the user is not logged in.
 * If the user is not logged in, it renders the Outlet; otherwise, it redirects to the home page.
 * 
 * @returns JSX element representing the anonymous user route.
 */
const AnonymousUserRoute = () => {
  const location = useLocation();
  return !isUserLoggedIn() ? <Outlet /> : <Navigate to="/" replace state={{ from: location }} />;
};

/**
 * Router configuration for the application.
 */
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
