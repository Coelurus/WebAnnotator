import { Outlet, Link } from "react-router-dom";

export default function Root() {
    return (
      <>
        <div id="sidebar">
          <h1>Annotator</h1>
          <nav>
            <ul>
              <li>
                <Link to={`./login`}>Log in</Link>
              </li>
              <li>
                <Link to={`./signup`}>Sign up</Link>
              </li>
              <li>
                <Link to={`./projects/all`}>Projects</Link>
              </li>
              <li>
                <Link to={`./projects/create`}>New Project</Link>
              </li>
              <li>
                <Link to={`./users`}>Users</Link>
              </li>
              <li>
                <Link to={`./teams`}>Teams</Link>
              </li>
              <li>
                <Link to={`./logout`}>Logout</Link>
              </li>
            </ul>
          </nav>
        </div>
        <div id="detail">
            <Outlet />
        </div>
      </>
    );
  }