import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { ProjectResponse } from '../../../persistence/model/responses';
import { mapProjectResponses } from '../../../persistence/mapper/mapper';
import { request } from '../../../security/auth';
import { Button, Modal, Table } from 'react-bootstrap';
import { Pencil, Plus, Trash, X } from 'react-bootstrap-icons';
import ProjectForm from './add-project-modal';

interface ProjectInfo {
  id: number;
  name: string;
}

export default function Projects() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [projectToDelete, setProjectToDelete] = useState<ProjectInfo | null>(null);
  const [showDeleteProjectConfirmation, setShowDeleteProjectConfirmation] = useState(false);
  const [showAddProjectModal, setShowAddProjectModal] = useState(false);

  useEffect(() => {
    request('GET', '/api/projects')
      .then((response) => setProjects(mapProjectResponses(response.data)))
      .catch((error) => console.error('Error fetching projects:', error));
  }, [showAddProjectModal]);

  const handleProjectEdit = (id: number) => {
    alert('TODO: handleProjectEdit');
  };

  const handleProjectDelete = (projectId: number, projectName: string) => {
    setProjectToDelete({ id: projectId, name: projectName });
    setShowDeleteProjectConfirmation(true);
  };

  const handleDeleteProjectClose = () => setShowDeleteProjectConfirmation(false);

  const deleteProject = async () => {
    await request('DELETE', `/api/projects/${projectToDelete?.id}`);
    setProjectToDelete(null);
    window.location.reload();
  };

  const handleAddProjectShow = () => setShowAddProjectModal(true);

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
                <Link to={'/editor/' + project.id}>{project.projectName}</Link>
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
                  onClick={() => handleProjectDelete(project.id, project.projectName)}
                >
                  <Trash />
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Button variant="primary" className="mb-3" onClick={handleAddProjectShow}>
        <Plus /> Add Project
      </Button>

      <Modal show={showDeleteProjectConfirmation} onHide={handleDeleteProjectClose}>
        <Modal.Header closeButton>
          <Modal.Title>Do you really want to delete team?</Modal.Title>
        </Modal.Header>
        <Modal.Body className="d-flex justify-content-around">
          <Button variant="success" className="mb-3 me-5" onClick={handleDeleteProjectClose}>
            <X /> DO NOT DELETE
          </Button>
          <Button variant="danger" className="mb-3" onClick={deleteProject}>
            <X /> DELETE
          </Button>
        </Modal.Body>
      </Modal>

      <ProjectForm
        showAddProjectModal={showAddProjectModal}
        setShowAddProjectModal={setShowAddProjectModal}
      />
    </div>
  );
}
