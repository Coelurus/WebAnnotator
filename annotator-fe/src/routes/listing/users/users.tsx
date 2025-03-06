import React, { useState, useEffect } from 'react';
import { Table, Button, Form } from 'react-bootstrap';
import { Check, Pencil, Plus, Trash } from 'react-bootstrap-icons';
import { ShortTeamResponse, UserResponse } from '../../../persistence/model/responses';
import { fetchUsers } from '../../../persistence/fetcher/fetcher';
import { getUserUsername, request } from '../../../security/auth';
import AddUserModal from './add-user-modal';
import DeleteUserModal from './delete-user-modal';

export interface UserInfo {
  id: number,
  name: string
}

interface EditUserProps {
  username?: string,
  firstName?: string,
  lastName?: string,
  team?: ShortTeamResponse | null
}

export default function Users() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [showAddUserModal, setShowAddUserModal] = useState(false);
  const [showDeleteUserConfirmation, setShowDeleteUserConfirmation] = useState(false);
  const [userToDelete, setUserToDelete] = useState<UserInfo | null>(null);
  const [editMode, setEditMode] = useState<number | null>(null);
  const [editValues, setEditValues] = useState<EditUserProps>({});

  const handleAddUserShow = () => setShowAddUserModal(true);
  const handleUserDeleteShow = (userId: number, username: string) => {
    setUserToDelete({id: userId, name: username})
    setShowDeleteUserConfirmation(true);
  };
  const handleUserEdit = (user: UserResponse) => {
    setEditMode(user.id);
    setEditValues({ ...user });
  };
  const handleFieldChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>, 
    field: string
  ) => {
    setEditValues({ ...editValues, [field]: e.target.value });
  };
  const handleSubmitUserEdit = () => {
    request('PATCH', `/api/users/${editMode}`, editValues ); //TODO
    setEditMode(null);
  };

  useEffect(() => {
    fetchUsers().then(setUsers);
  }, []);

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
              {editMode === user.id ? (
              <>
                <td>
                  <Form.Control
                    type="text"
                    value={editValues.firstName}
                    onChange={(e) => handleFieldChange(e, "firstName")}
                  />
                  <Form.Control
                    type="text"
                    value={editValues.lastName}
                    onChange={(e) => handleFieldChange(e, "lastName")}
                  />
                </td>
                <td>
                  <Form.Control
                    type="text"
                    value={editValues.username}
                    onChange={(e) => handleFieldChange(e, "username")}
                  />
                </td>
                <td>
                  <Form.Control
                    type="text"
                    value={editValues.team ? editValues.team.name : ""}
                    onChange={(e) => handleFieldChange(e, "team")}
                  />
                </td>
                <td>
                  <Button variant="success" size="sm" onClick={handleSubmitUserEdit}>
                    <Check />
                  </Button>
                </td>
              </>
            ) : (
              <>
                <td>{user.firstName} {user.lastName}</td>
                <td>
                  {user.username}
                  <b>{getUserUsername() === user.username ? " (you)" : ""}</b>
                </td>
                <td>{user.team ? user.team.name : "-"}</td>
                <td>
                  <Button variant="warning" className="me-2" size="sm" onClick={() => handleUserEdit(user)}>
                    <Pencil />
                  </Button>
                  <Button variant="danger" size="sm" onClick={() => handleUserDeleteShow(user.id, user.username)}>
                    <Trash />
                  </Button>
                </td>
              </>
            )}
            </tr>
          ))}
        </tbody>
      </Table>

      <Button variant="primary" className="mb-3" onClick={handleAddUserShow}>
        <Plus /> Add User
      </Button>

      <AddUserModal
        showAddUserModal={showAddUserModal} 
        setShowAddUserModal={setShowAddUserModal} 
      />

      <DeleteUserModal 
        showDeleteUserConfirmation={showDeleteUserConfirmation} 
        setShowDeleteUserConfirmation={setShowDeleteUserConfirmation}
        userToDelete={userToDelete} 
        setUserToDelete={setUserToDelete}        
      />      
    </div>
  );
}
