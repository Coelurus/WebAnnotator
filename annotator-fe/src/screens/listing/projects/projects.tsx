import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { LongTeam, Priority, Progress, Project } from '../../../persistence/model/data';
import { Check, Pencil, Plus, Trash, X } from 'react-bootstrap-icons';
import ProjectForm from './add-project-modal';
import {
  fetchPriorities,
  fetchProgresses,
  fetchProjects,
  fetchTeams
} from '../../../persistence/requests/fetcher';
import { deleteProjectRequest } from '../../../persistence/requests/deleter';
import { updateProject } from '../../../persistence/requests/updater';
import { ProjectRequest } from '../../../persistence/model/requests';
import { mapProjectRequest } from '../../../persistence/mapper/mapper';
import Table from 'react-bootstrap/esm/Table';
import Button from 'react-bootstrap/esm/Button';
import Form from 'react-bootstrap/esm/Form';
import Modal from 'react-bootstrap/esm/Modal';
import { isUserAdmin } from '../../../security/auth';

interface ProjectInfo {
  id: number;
  name: string;
}

export default function Projects() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [projectToDelete, setProjectToDelete] = useState<ProjectInfo | null>(null);
  const [showDeleteProjectConfirmation, setShowDeleteProjectConfirmation] = useState(false);
  const [showAddProjectModal, setShowAddProjectModal] = useState(false);
  const [editProjectId, setEditProjectId] = useState<number | null>(null);
  const [editProjectValues, setEditProjectValues] = useState<ProjectRequest>({});
  const [progresses, setProgresses] = useState<Progress[]>([]);
  const [priorities, setPriorities] = useState<Priority[]>([]);
  const [teams, setTeams] = useState<LongTeam[]>([]);

  useEffect(() => {
    if (isUserAdmin()) {
      fetchTeams().then(setTeams);
    }
    fetchProgresses().then(setProgresses);
    fetchPriorities().then(setPriorities);
  }, [editProjectId !== null]);

  useEffect(() => {
    fetchProjects().then(setProjects);
  }, [showAddProjectModal, projectToDelete, editProjectId === null]);

  const handleProjectEdit = (project: Project) => {
    setEditProjectId(project.id);
    setEditProjectValues({ ...mapProjectRequest(project) });
  };

  const handleProjectFieldChange = (
    field: keyof ProjectRequest,
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    setEditProjectValues({ ...editProjectValues, [field]: e.target.value });
  };

  const handleSelectChange = (field: keyof ProjectRequest, team: LongTeam | undefined) => {
    setEditProjectValues({ ...editProjectValues, [field]: team ? team.id : null });
  };

  const handleSubmitProjectEdit = () => {
    if (editProjectId === null) return;
    updateProject(editProjectId, editProjectValues).then(() => setEditProjectId(null));
  };

  const handleCancelProjectEdit = () => setEditProjectId(null);

  const handleProjectDelete = (projectId: number, projectName: string) => {
    setProjectToDelete({ id: projectId, name: projectName });
    setShowDeleteProjectConfirmation(true);
  };

  const handleDeleteProjectClose = () => setShowDeleteProjectConfirmation(false);

  const deleteProject = async () => {
    deleteProjectRequest(projectToDelete?.id ?? 0).then(() => {
      setProjectToDelete(null);
      handleDeleteProjectClose();
    });
  };

  const handleAddProjectShow = () => setShowAddProjectModal(true);

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Projects</h1>
      <Table striped bordered hover responsive>
        <thead className="table-dark">
          <tr>
            <th>Project Name</th>
            <th>Progress</th>
            <th>Deadline</th>
            <th>Priority</th>
            {isUserAdmin() && (
              <>
                <th>Team</th>
                <th>Actions</th>
              </>
            )}
          </tr>
        </thead>
        <tbody>
          {projects.map((project) => (
            <tr key={project.id}>
              {editProjectId !== project.id ? (
                <>
                  <td>
                    <Link to={'/editor/' + project.id}>{project.projectName}</Link>
                  </td>
                  <td>{project.progress}</td>
                  <td>{project.deadline}</td>
                  <td>{project.priority}</td>
                  {isUserAdmin() && (
                    <>
                      <td>{project.team?.name ?? '-'}</td>
                      <td>
                        <Button
                          variant="warning"
                          className="me-2"
                          size="sm"
                          onClick={() => handleProjectEdit(project)}
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
                    </>
                  )}
                </>
              ) : (
                <>
                  <td>
                    <Form.Control
                      type="text"
                      value={editProjectValues.projectName}
                      onChange={(e) => handleProjectFieldChange('projectName', e)}
                    />
                  </td>
                  <td>
                    <Form.Select
                      defaultValue={project.progress}
                      onChange={(e) => handleProjectFieldChange('progress', e)}
                    >
                      {progresses.map((progress) => (
                        <option key={`progress_${progress.value}`} value={progress.name}>
                          {progress.name}
                        </option>
                      ))}
                    </Form.Select>
                  </td>
                  <td>
                    <Form.Control
                      type="date"
                      value={editProjectValues.deadline}
                      onChange={(e) => handleProjectFieldChange('deadline', e)}
                    />
                  </td>
                  <td>
                    <Form.Select
                      defaultValue={project.priority}
                      onChange={(e) => handleProjectFieldChange('priority', e)}
                    >
                      {priorities.map((priority) => (
                        <option key={`priority_${priority.value}`} value={priority.name}>
                          {priority.name}
                        </option>
                      ))}
                    </Form.Select>
                  </td>
                  <td>
                    <Form.Select
                      defaultValue={project.team?.id}
                      onChange={(e) =>
                        handleSelectChange(
                          'teamId',
                          teams.find((t) => t.id === Number(e.target.value))
                        )
                      }
                    >
                      <option value={undefined}>-</option>
                      {teams.map((team) => (
                        <option key={`team_${team.id}`} value={team.id}>
                          {team.name}
                        </option>
                      ))}
                    </Form.Select>
                  </td>
                  <td>
                    <Button variant="success" size="sm" onClick={handleSubmitProjectEdit}>
                      <Check />
                    </Button>
                    <Button variant="outline-danger" size="sm" onClick={handleCancelProjectEdit}>
                      <X />
                    </Button>
                  </td>
                </>
              )}
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
