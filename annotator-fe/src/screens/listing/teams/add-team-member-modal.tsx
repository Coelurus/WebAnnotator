import React from 'react';
import Form from 'react-bootstrap/esm/Form';
import Modal from 'react-bootstrap/esm/Modal';
import { LongTeam, LongUser } from '../../../persistence/model/data';
import { addTeamMember } from '../../../persistence/requests/poster';
import Button from 'react-bootstrap/esm/Button';

/**
 * Interface for the properties of the AddMemberModal component.
 */
export interface AddMemberModalProps {
  /**
   * Boolean to control the visibility of the modal.
   */
  showAddMemberModal: boolean;
  /**
   * Function to set the visibility state of the modal.
   */
  setShowAddMemberModal: React.Dispatch<React.SetStateAction<boolean>>;
  /**
   * Array of users to be displayed in the member selection dropdown.
   */
  users: LongUser[];
  /**
   * The team to which the member will be added.
   */
  teamToAddMemberTo: LongTeam | null | undefined;
}

/**
 * Component for adding a new member to a team through a modal form.
 *
 * @param props The properties for the AddMemberModal component.
 * @returns JSX Element representing the add member form modal.
 */
export default function AddMemberModal({
  showAddMemberModal,
  setShowAddMemberModal,
  users,
  teamToAddMemberTo
}: AddMemberModalProps) {
  // State to manage the selected user to be added as a member
  const [selectedUser, setSelectedUser] = React.useState<LongUser | null>();

  /**
   * Function to close the modal for adding a new member.
   */
  const handleAddMemberClose = () => setShowAddMemberModal(false);

  /**
   * Function to handle the submission of the add member form.
   *
   * @param e The form submission event.
   */
  const handleAddMemberSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!teamToAddMemberTo || !selectedUser) {
      setSelectedUser(null);
      handleAddMemberClose();
      return;
    }
    addTeamMember(selectedUser.id, teamToAddMemberTo.id).then(() => {
      setSelectedUser(null);
      handleAddMemberClose();
    });
  };

  /**
   * Function to handle changes in the team member selection.
   *
   * @param user The selected user or undefined if no user is selected.
   */
  const handleTeamMemberChange = (user: LongUser | undefined) => {
    if (user) {
      setSelectedUser(user);
    }
  };

  return (
    <Modal show={showAddMemberModal} onHide={handleAddMemberClose}>
      <Modal.Header closeButton>
        <Modal.Title>Add User</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={(e) => handleAddMemberSubmit(e)}>
          <Form.Group className="mb-3">
            <Form.Label>Select user to add</Form.Label>
            <Form.Select
              defaultValue={users[0]?.id}
              onChange={(e) =>
                handleTeamMemberChange(users.find((u) => u.id === Number(e.target.value)))
              }
            >
              {users
                .filter((u) => u.team?.id !== teamToAddMemberTo?.id)
                .map((user) => (
                  <option value={user.id} key={`leader-id-${user.id}`}>
                    {user.firstName} {user.lastName}
                  </option>
                ))}
            </Form.Select>
          </Form.Group>
          <Button variant="primary" type="submit">
            Add Member
          </Button>
        </Form>
      </Modal.Body>
    </Modal>
  );
}
