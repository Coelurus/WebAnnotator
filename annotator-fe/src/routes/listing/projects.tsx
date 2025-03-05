import React, { useEffect, useState } from 'react';
import { Link } from "react-router-dom";

import { ProjectResponse } from '../../persistence/model/responses';
import { mapProjectResponses } from '../../persistence/mapper/mapper';
import { request } from '../../security/auth';
import { Button, Table } from 'react-bootstrap';
import { Pencil, Trash } from 'react-bootstrap-icons';



export default function Projects() {
    const [projects, setProjects] = useState<ProjectResponse[]>([]);

    useEffect(() => {
      request("GET", "/api/projects")
        .then((response) => setProjects(mapProjectResponses(response.data)))
        .catch((error) => console.error("Error fetching projects:", error));
      }, []);      

  function handleProjectEdit(id: number): void {
    alert("TODO: handleProjectEdit");
  }

  function handleProjectDelete(id: number): void {
    alert("TODO: handleProjectDelete");
  }

      return (
        <div className="container mt-4">
          <h1 className="mb-4">Projects</h1>
          <Table striped bordered hover responsive>
            <thead className="table-dark">
              <tr>
                <th>Project Name</th>
                <th>Log File Name</th>
                <th>Deadline</th>
                <th>Priority</th>
                <th>Team</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {projects.map((project) => (
                <tr key={project.id}>
                    <td>
                      <Link to={"/projects/" + project.id}>
                        {project.projectName}
                      </Link>
                    </td>
                    <td>{project.logFileName}</td>
                    <td>{project.deadline}</td>
                    <td>{project.priority}</td>
                    <td>{project.team.name}</td>
                    <td>
                    <Button 
                      variant="warning" 
                      className="me-2" 
                      size="sm"
                      onClick={() => handleProjectEdit(project.id)}
                    >
                      <Pencil />
                    </Button>
                    <Button 
                                              variant="danger" 
                      size="sm"
              className="me-2" 
                      onClick={() => handleProjectDelete(project.id)}
                    >
                      <Trash />
                    </Button>
                    </td>
                  </tr>
              ))}
            </tbody>
          </Table>
        </div>
      );
}