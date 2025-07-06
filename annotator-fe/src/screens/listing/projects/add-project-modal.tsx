import React from 'react';

import { LongTeam, Priority } from '../../../persistence/model/data';
import { fetchPriorities, fetchTeams } from '../../../persistence/requests/fetcher';
import Modal from 'react-bootstrap/esm/Modal';
import Form from 'react-bootstrap/esm/Form';
import Button from 'react-bootstrap/esm/Button';
import { ProjectRequest } from '../../../persistence/model/requests';
import { createProjectRequest } from '../../../persistence/requests/poster';
import { isUserAdmin } from '../../../security/auth';

/**
 * Interface for the properties of the AddProjectModal component.
 */
export interface AddProjectModalProps {
  /**
   * Boolean to control the visibility of the modal.
   */
  showAddProjectModal: boolean;
  /**
   * Function to set the visibility state of the modal.
   */
  setShowAddProjectModal: React.Dispatch<React.SetStateAction<boolean>>;
}

/**
 * Component for adding a new project through a modal form.
 *
 * @param props The properties for the AddProjectModal component.
 * @returns JSX Element representing the add project form modal.
 */
export default function ProjectForm({
  showAddProjectModal,
  setShowAddProjectModal
}: Readonly<AddProjectModalProps>) {
  // State to manage list of all teams
  const [teams, setTeams] = React.useState<LongTeam[]>([]);
  // State to manage list of available priorities
  const [priorities, setPriorities] = React.useState<Priority[]>([]);
  // State to manage creation of a new project
  const [newProject, setNewProject] = React.useState<ProjectRequest>({});

  // Effect to fetch teams and priorities when the component mounts
  React.useEffect(() => {
    if (isUserAdmin()) {
      fetchTeams().then(setTeams);
    }
    fetchPriorities().then(setPriorities);
  }, []);

  /**
   * Function to close the modal for adding a new project.
   */
  const handleAddProjectClose = () => setShowAddProjectModal(false);

  /**
   * Function to handle changes in the project form fields.
   *
   * @param value The new value for the field.
   * @param name The name of the field being changed.
   */
  const handleAddProjectChange = (value: string, name: string) => {
    setNewProject({ ...newProject, [name]: value });
  };

  /**
   * Function to handle file selection for the project.
   *
   * @param e The change event from the file input.
   */
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0];
      setNewProject((prev) => ({
        ...prev,
        file: file
      }));
    }
  };

  /**
   * Function to handle changes in the team selection dropdown.
   *
   * @param field The field name to update (e.g., 'teamId').
   * @param team The selected team object or undefined if no team is selected.
   */
  const handleSelectChange = (field: string, team: LongTeam | undefined) => {
    setNewProject({ ...newProject, [field]: team ? team.id : null });
  };

  /**
   * Function to handle the submission of the new project form.
   *
   * @param e The submit event from the form.
   */
  const handleAddProjectSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const formData = new FormData();
    Object.entries(newProject).forEach(([key, value]) => {
      if (value instanceof File) {
        formData.append(key, value);
      } else {
        formData.append(key, String(value));
      }
    });

    if (!newProject.priority) {
      formData.append('priority', priorities[0]?.name);
    }
    if (!newProject.deadline) {
      formData.append('deadline', defaultDeadlineInWeek());
    }

    createProjectRequest(formData).then(() => {
      setNewProject({});
      handleAddProjectClose();
    });
  };

  /**
   * Function to get the default deadline for a new project, set to one week from today.
   *
   * @returns A string representing the default deadline date in 'YYYY-MM-DD' format.
   */
  const defaultDeadlineInWeek = () => {
    const nextWeek = new Date();
    nextWeek.setDate(nextWeek.getDate() + 7);
    return nextWeek.toISOString().split('T')[0];
  };

  return (
    <Modal show={showAddProjectModal} onHide={handleAddProjectClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>Create New Project</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={(e) => handleAddProjectSubmit(e)} encType="multipart/form-data">
          <Form.Group className="mb-3">
            <Form.Label>Project Name</Form.Label>
            <Form.Control
              type="text"
              name="projectName"
              placeholder="Project Name"
              onChange={(e) => handleAddProjectChange(e.target.value, e.target.name)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>File to Upload</Form.Label>
            <Form.Control type="file" name="file" onChange={handleFileChange} />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Deadline</Form.Label>
            <Form.Control
              type="date"
              defaultValue={defaultDeadlineInWeek()}
              name="deadline"
              placeholder="Deadline"
              onChange={(e) => handleAddProjectChange(e.target.value, e.target.name)}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Priority</Form.Label>
            <Form.Select
              name="priority"
              required
              onChange={(e) => handleAddProjectChange(e.target.value, e.target.name)}
              defaultValue={priorities[0]?.name}
            >
              {priorities
                .sort((a, b) => a.value - b.value)
                .map((priority) => (
                  <option value={priority.name} key={priority.name}>
                    {priority.name}
                  </option>
                ))}
            </Form.Select>
          </Form.Group>
          {isUserAdmin() && (
            <Form.Group className="mb-3">
              <Form.Label>Team</Form.Label>
              <Form.Select
                required
                onChange={(e) =>
                  handleSelectChange(
                    'teamId',
                    teams.find((t) => t.id === Number(e.target.value))
                  )
                }
              >
                <option value={undefined}>All</option>
                {teams.map((team) => (
                  <option value={team.id} key={team.id}>
                    {team.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          )}
          <Button variant="primary" type="submit">
            Upload
          </Button>
        </Form>
      </Modal.Body>
    </Modal>
  );
}
