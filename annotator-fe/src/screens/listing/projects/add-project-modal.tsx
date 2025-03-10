import React from 'react';

import { LongTeam, Priority } from '../../../persistence/model/data';
import { fetchPriorities, fetchTeams } from '../../../persistence/requests/fetcher';
import Modal from 'react-bootstrap/esm/Modal';
import Form from 'react-bootstrap/esm/Form';
import Button from 'react-bootstrap/esm/Button';
import { ProjectRequest } from '../../../persistence/model/requests';
import { createProjectRequest } from '../../../persistence/requests/poster';

interface AddProjectModalProps {
  showAddProjectModal: boolean;
  setShowAddProjectModal: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function ProjectForm({
  showAddProjectModal,
  setShowAddProjectModal
}: AddProjectModalProps) {
  const [teams, setTeams] = React.useState<LongTeam[]>([]);
  const [priorities, setPriorities] = React.useState<Priority[]>([]);
  const [newProject, setNewProject] = React.useState<ProjectRequest>({});

  React.useEffect(() => {
    fetchTeams().then(setTeams);
  }, []);
  React.useEffect(() => {
    fetchPriorities().then(setPriorities);
  }, []);

  const handleAddProjectClose = () => setShowAddProjectModal(false);

  const handleAddProjectChange = (value: string, name: string) => {
    setNewProject({ ...newProject, [name]: value });
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0];
      setNewProject((prev) => ({
        ...prev,
        file: file
      }));
    }
  };

  const handleSelectChange = (field: string, team: LongTeam | undefined) => {
    setNewProject({ ...newProject, [field]: team ? team.id : null });
  };

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

    createProjectRequest(formData, (message) => alert(message)).then(() => {
      setNewProject({});
      handleAddProjectClose();
    });
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
            >
              <option value="">None</option>
              {priorities.map((priority) => (
                <option value={priority.name} key={priority.name}>
                  {priority.name}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
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
              <option value="">All</option>
              {teams.map((team) => (
                <option value={team.id} key={team.id}>
                  {team.name}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
          <Button variant="primary" type="submit">
            Upload
          </Button>
        </Form>
      </Modal.Body>
    </Modal>
  );
}
