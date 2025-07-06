import React from 'react';
import Button from 'react-bootstrap/esm/Button';
import Form from 'react-bootstrap/esm/Form';
import Modal from 'react-bootstrap/esm/Modal';
import { TeamRequest } from '../../../persistence/model/requests';
import { createTeamRequest } from '../../../persistence/requests/poster';
import { LongUser } from '../../../persistence/model/data';
import { fetchUsers } from '../../../persistence/requests/fetcher';

/**
 * Interface for the properties of the AddTeamModal component.
 */
export interface AddTeamModalProps {
  /**
   * Boolean to control the visibility of the modal.
   */
  showAddTeamModal: boolean;
  /**
   * Function to set the visibility state of the modal.
   */
  setShowAddTeamModal: React.Dispatch<React.SetStateAction<boolean>>;
  /**
   * Array of users to be displayed in the team leader selection dropdown.
   */
  users: LongUser[];
  /**
   * Function to set the users state.
   */
  setUsers: React.Dispatch<React.SetStateAction<LongUser[]>>;
}

/**
 * Component for adding a new team through a modal form.
 *
 * @param props The properties for the AddTeamModal component.
 * @returns JSX Element representing the add team form modal.
 */
export default function AddTeamModal({ showAddTeamModal, setShowAddTeamModal, users, setUsers }: AddTeamModalProps) {
  // State to manage the new team being created
  const [newTeam, setNewTeam] = React.useState<TeamRequest>({});

  // Effect to fetch users when the component mounts
  // TODO: just reuse users
  React.useEffect(() => {
    fetchUsers().then((response) => {
      setUsers(response);
      handleTeamLeaderChange('leaderId', response[0]);
    });
  }, []);

  /**
   * Function to close the modal for adding a new team.
   */
  const handleAddTeamClose = () => setShowAddTeamModal(false);

  /**
   * Function to handle the submission of the add team form.
   *
   * @param e The form submission event.
   */
  const handleAddTeamSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    createTeamRequest(newTeam).then(() => {
      setNewTeam({});
      handleAddTeamClose();
    });
  };

  /**
   * Function to handle changes in the team form fields.
   *
   * @param value The new value for the field.
   * @param name The name of the field being changed.
   */
  const handleAddTeamChange = (value: string, name: string) => {
    setNewTeam({ ...newTeam, [name]: value });
  };

  /**
   * Function to handle changes in the team leader selection.
   *
   * @param field The field being updated
   * @param user The selected user or undefined if no user is selected.
   */
  const handleTeamLeaderChange = (field: string, user: LongUser | undefined) => {
    setNewTeam({ ...newTeam, [field]: user ? user.id : null });
  };

  return (
    <Modal show={showAddTeamModal} onHide={handleAddTeamClose}>
      <Modal.Header closeButton>
        <Modal.Title>Add User</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={(e) => handleAddTeamSubmit(e)}>
          <Form.Group className="mb-3">
            <Form.Label>Team Name</Form.Label>
            <Form.Control
              type="text"
              name="name"
              onChange={(e) => handleAddTeamChange(e.target.value, e.target.name)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Team Leader</Form.Label>
            <Form.Select
              defaultValue={users[0]?.id}
              onChange={(e) =>
                handleTeamLeaderChange(
                  'leaderId',
                  users.find((u) => u.id === Number(e.target.value))
                )
              }
            >
              {users.map((user) => (
                <option value={user.id} key={`leader-id-${user.id}`}>
                  {user.firstName} {user.lastName}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
          <Button variant="primary" type="submit">
            Add Team
          </Button>
        </Form>
      </Modal.Body>
    </Modal>
  );
}
