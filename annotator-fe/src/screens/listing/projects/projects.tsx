import React from 'react';
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

/**
 * Interface to identify a project for deletion
 */
interface ProjectInfo {
  /**
   * ID of the project to be deleted
   */
  id: number;
  /**
   * Name of the project to be deleted
   */
  name: string;
}

/**
 * Projects component displays a list of projects with options to edit, delete, and add new projects.
 * It fetches project data, manages state for editing and deleting projects, and renders a table of projects.
 *
 * @returns JSX Element representing the projects listing screen.
 */
export default function Projects() {
  // State to manage the list of projects
  const [projects, setProjects] = React.useState<Project[]>([]);
  // State to manage the project to be deleted
  const [projectToDelete, setProjectToDelete] = React.useState<ProjectInfo | null>(null);
  // State to manage the visibility of the delete confirmation modal
  const [showDeleteProjectConfirmation, setShowDeleteProjectConfirmation] = React.useState(false);
  // State to manage the visibility of the add project modal
  const [showAddProjectModal, setShowAddProjectModal] = React.useState(false);
  // State to manage the project being edited
  const [editProjectId, setEditProjectId] = React.useState<number | null>(null);
  // State to manage the values of the project being edited
  const [editProjectValues, setEditProjectValues] = React.useState<ProjectRequest>({});
  // State to manage the list of progresses
  const [progresses, setProgresses] = React.useState<Progress[]>([]);
  // State to manage the list of priorities
  const [priorities, setPriorities] = React.useState<Priority[]>([]);
  // State to manage the list of teams
  const [teams, setTeams] = React.useState<LongTeam[]>([]);

  // Effect to update teams, progresses, and priorities when editing a project
  React.useEffect(() => {
    if (isUserAdmin()) {
      fetchTeams().then(setTeams);
    }
    fetchProgresses().then(setProgresses);
    fetchPriorities().then(setPriorities);
  }, [editProjectId !== null]);

  // Effect to update projects when state of adding, deleting, or editing a project changes
  React.useEffect(() => {
    fetchProjects().then(setProjects);
  }, [showAddProjectModal, projectToDelete, editProjectId === null]);

  /**
   * Function to handle the editing of a project.
   * 
   * @param project The project object to be edited.
   */
  const handleProjectEdit = (project: Project) => {
    setEditProjectId(project.id);
    setEditProjectValues({ ...mapProjectRequest(project) });
  };

  /**
   * Function to handle changes in the project form fields.
   * 
   * @param field The field name to update.
   * @param e The change event from the input field.
   */
  const handleProjectFieldChange = (
    field: keyof ProjectRequest,
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    setEditProjectValues({ ...editProjectValues, [field]: e.target.value });
  };

  /**
   * Function to handle changes in the team selection dropdown.
   * 
   * @param field The field name to update.
   * @param team The selected team object or undefined if no team is selected.
   */
  const handleSelectChange = (field: keyof ProjectRequest, team: LongTeam | undefined) => {
    setEditProjectValues({ ...editProjectValues, [field]: team ? team.id : null });
  };

  /**
   * Function to handle the submission of the edited project form.
   */
  const handleSubmitProjectEdit = () => {
    if (editProjectId === null) return;
    updateProject(editProjectId, editProjectValues).then(() => setEditProjectId(null));
  };

  /**
   * Function to handle the cancellation of project editing.
   */
  const handleCancelProjectEdit = () => setEditProjectId(null);

  /**
   * Function to handle clicking on the delete button for a project.
   * 
   * @param projectId ID of the project to be deleted.
   * @param projectName Name of the project to be deleted.
   */
  const handleProjectDelete = (projectId: number, projectName: string) => {
    setProjectToDelete({ id: projectId, name: projectName });
    setShowDeleteProjectConfirmation(true);
  };

  /**
   * Function to close the delete project confirmation modal.
   */
  const handleDeleteProjectClose = () => setShowDeleteProjectConfirmation(false);

  /**
   * Function to delete a project.
   */
  const deleteProject = async () => {
    deleteProjectRequest(projectToDelete?.id ?? 0).then(() => {
      setProjectToDelete(null);
      handleDeleteProjectClose();
    });
  };

  /**
   * Function to show the add project modal.
   */
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
