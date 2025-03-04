import { Outlet, Link } from "react-router-dom";
import React from "react"
import { isUserAdmin, isUserLoggedIn, } from "../security/auth";
import LogoutButton from "./security/logout/logout-screen";


export default function Root() {
  return (
    <>
      <div id="sidebar">
        <h1>Annotator</h1>
        <nav>
          <ul>
            {!isUserLoggedIn() && (
              <>
                <li>
                  <Link to={`./login`}>Log in</Link>
                </li>
                <li>
                  <Link to={`./signup`}>Sign up</Link>
                </li>
              </>
            )}
            {isUserLoggedIn() && (
              <>
                <li>
                  <Link to={`./projects/all`}>Projects</Link>
                </li>
                <li>
                  <Link to={`./projects/create`}>New Project</Link>
                </li>
              </>
            )}
            {isUserAdmin() && (
              <>
                <li>
                  <Link to={`./admin/users`}>Users</Link>
                </li>
                <li>
                  <Link to={`./admin/teams`}>Teams</Link>
                </li>
              </>
            )}
            {isUserLoggedIn() &&
              (<li>
                <LogoutButton />
              </li>
            )}
          </ul>
        </nav>
      </div>
      <div id="detail">
          <Outlet />
      </div>
    </>
  );
}