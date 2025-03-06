import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Modal from 'react-bootstrap/Modal';
import React from 'react';
import { request } from '../../../security/auth';
import { UserRequest } from '../../../persistence/model/requests';

interface AddUserModalProps {
  showAddUserModal: boolean;
  setShowAddUserModal: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function AddUserModal({ showAddUserModal, setShowAddUserModal }: AddUserModalProps) {
  const [newUser, setNewUser] = React.useState<UserRequest>({});
  const handleAddUserClose = () => setShowAddUserModal(false);
  const handleAddUserChange = (value: string, name: string) => {
    setNewUser({ ...newUser, [name]: value });
  };

  const handleAddUserSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    request('POST', '/api/users', newUser);

    setNewUser({});
    handleAddUserClose();
    window.location.reload();
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
              placeholder='First Name'
              value={newUser.firstName}
              onChange={(e) => handleAddUserChange(e.target.value, e.target.name)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Last Name</Form.Label>
            <Form.Control
              type="text"
              name="lastName"
              placeholder='Last Name'
              value={newUser.lastName}
              onChange={(e) => handleAddUserChange(e.target.value, e.target.name)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Username</Form.Label>
            <Form.Control
              type="text"
              name="username"
              placeholder='Username'
              value={newUser.username}
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
