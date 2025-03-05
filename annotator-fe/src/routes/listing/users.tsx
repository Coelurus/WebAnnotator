import React, { useState, useEffect, FormEvent } from 'react';
import { Table, Button, Modal, Form } from 'react-bootstrap';
import { Pencil, Plus, Trash, X } from 'react-bootstrap-icons';
import { UserResponse } from '../../persistence/model/responses';
import { fetchUsers } from '../../persistence/fetcher/fetcher';
import { getUserUsername, invalidateToken, request } from '../../security/auth';

export default function Users() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [showAddUserModal, setShowAddUserModal] = useState(false);
  const [showDeleteUserConfirmation, setShowDeleteUserConfirmation] = useState(false);
  const [newUser, setNewUser] = useState({ firstName: '', lastName: '', username: '' });

  const handleAddUserShow = () => setShowAddUserModal(true);
  const handleAddUserClose = () => setShowAddUserModal(false);
  const handleAddUserChange = (value: string, name: string) => {
    setNewUser({ ...newUser, [name]: value });
  };
  const handleAddUserSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    alert('Here should happen smth like : handleUserAdd(newUser)');
    setNewUser({ firstName: '', lastName: '', username: '' });
    handleAddUserClose();
  };

  const [userIdToDelete, setUserIdToDelete] = useState<number | null>(null);
  const [usernameToDelete, setUsernameToDelete] = useState<string | null>(null);
  const handleDeleteUserClose = () => setShowDeleteUserConfirmation(false);

  const deleteUser = async () => {
    await request('DELETE', `/api/users/${userIdToDelete}`);
    setUserIdToDelete(null);
    setShowDeleteUserConfirmation(false);
    if (getUserUsername() === usernameToDelete) {
      invalidateToken();
    }
    window.location.reload();
  };

  useEffect(() => {
    fetchUsers().then(setUsers);
  }, []);

  const handleUserEdit = (userId: number) => {
    alert('TODO: Edit user: ' + userId);
  };

  const handleUserDelete = (userId: number, username: string) => {
    setUserIdToDelete(userId);
    setUsernameToDelete(username);
    setShowDeleteUserConfirmation(true);
  };

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Users</h1>
      <Table striped bordered hover responsive>
        <thead className="table-dark">
          <tr>
            <th>Name</th>
            <th>Username</th>
            <th>Team</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id} className="align-middle">
              <td>
                {user.firstName} {user.lastName}
              </td>
              <td>
                {user.username}
                <b>{getUserUsername() === user.username ? ' (you)' : ''}</b>
              </td>
              <td>{user.team ? user.team.name : '-'}</td>
              <td>
                <Button
                  variant="warning"
                  className="me-2"
                  size="sm"
                  onClick={() => handleUserEdit(user.id)}
                >
                  <Pencil />
                </Button>
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => handleUserDelete(user.id, user.username)}
                >
                  <Trash />
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <Button variant="primary" className="mb-3" onClick={handleAddUserShow}>
        <Plus /> Add User
      </Button>
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

      <Modal show={showDeleteUserConfirmation} onHide={handleDeleteUserClose}>
        <Modal.Header closeButton>
          <Modal.Title>Do you really want to delete user?</Modal.Title>
        </Modal.Header>
        <Modal.Body className="d-flex justify-content-around">
          <Button variant="success" className="mb-3 me-5" onClick={handleDeleteUserClose}>
            <X /> DO NOT DELETE
          </Button>
          <Button variant="danger" className="mb-3" onClick={deleteUser}>
            <X /> DELETE
          </Button>
        </Modal.Body>
      </Modal>
    </div>
  );
}
