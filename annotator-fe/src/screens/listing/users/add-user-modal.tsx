import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Modal from 'react-bootstrap/Modal';
import React from 'react';
import { UserRequest } from '../../../persistence/model/requests';
import { createUserRequest } from '../../../persistence/requests/poster';

/**
 * Interface for the properties of the AddUserModal component.
 */
export interface AddUserModalProps {
  /**
   * Boolean to control the visibility of the modal.
   */
  showAddUserModal: boolean;
  /**
   * Function to set the visibility state of the modal.
   */
  setShowAddUserModal: React.Dispatch<React.SetStateAction<boolean>>;
}

/**
 * Component for adding a new user through a modal form.
 *
 * @param props The properties for the AddUserModal component.
 * @returns JSX Element representing the add user form modal.
 */
export default function AddUserModal({ showAddUserModal, setShowAddUserModal }: AddUserModalProps) {
  // State to manage the new user being created
  const [newUser, setNewUser] = React.useState<UserRequest>({});

  /**
   * Function to close the modal for adding a new user.
   */
  const handleAddUserClose = () => setShowAddUserModal(false);

  /**
   * Function to handle changes in the user form fields.
   *
   * @param value The new value for the field.
   * @param name The name of the field being changed.
   */
  const handleAddUserChange = (value: string, name: string) => {
    setNewUser({ ...newUser, [name]: value });
  };

  /**
   * Function to handle the submission of the add user form.
   *
   * @param e The form submission event.
   */
  const handleAddUserSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    createUserRequest(newUser).then(() => {
      setNewUser({});
      handleAddUserClose();
    });
  };

  return (
    <Modal show={showAddUserModal} onHide={handleAddUserClose}>
      <Modal.Header closeButton>
        <Modal.Title>Add User</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={(e) => handleAddUserSubmit(e)}>
          <Form.Group className="mb-3">
            <Form.Label>First Name</Form.Label>
            <Form.Control
              type="text"
              name="firstName"
              placeholder="First Name"
              onChange={(e) => handleAddUserChange(e.target.value, e.target.name)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Last Name</Form.Label>
            <Form.Control
              type="text"
              name="lastName"
              placeholder="Last Name"
              onChange={(e) => handleAddUserChange(e.target.value, e.target.name)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Username</Form.Label>
            <Form.Control
              type="text"
              name="username"
              placeholder="Username"
              onChange={(e) => handleAddUserChange(e.target.value, e.target.name)}
              required
            />
          </Form.Group>
          <Button variant="primary" type="submit">
            Add User
          </Button>
        </Form>
      </Modal.Body>
    </Modal>
  );
}
