import React, { useEffect, useState } from 'react';
import { Link } from "react-router-dom";

import { ProjectResponse } from '../../persistence/model/responses';
import { mapProjectResponses } from '../../persistence/mapper/mapper';
import { request } from '../../security/auth';



export default function Projects() {
    const [projects, setProjects] = useState<ProjectResponse[]>([]);

    useEffect(() => {
      request("GET", "/api/projects")
        .then((response) => setProjects(mapProjectResponses(response.data)))
        .catch((error) => console.error("Error fetching projects:", error));
      }, []);      

      return (
        <div>
          <h1>Projects</h1>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Project Name</th>
                <th>Log File Name</th>
                <th>Deadline</th>
                <th>Priority</th>
                <th>Team</th>
              </tr>
            </thead>
            <tbody>
              {projects.map((project) => (
                <tr key={project.id}>
                    <td>{project.id}</td>
                    <td>
                      <Link to={"/projects/" + project.id}>
                        {project.projectName}
                      </Link>
                    </td>
                    <td>{project.logFileName}</td>
                    <td>{project.deadline}</td>
                    <td>{project.priority}</td>
                    <td>{project.team.name}</td>
                  </tr>
              ))}
            </tbody>
          </table>
        </div>
      );
}