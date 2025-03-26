import React, {useEffect, useState} from 'react';
import {Button, Form, Table} from 'react-bootstrap';
import {Check, Pencil, Plus, Trash, X} from 'react-bootstrap-icons';
import {fetchRoles, fetchTeams, fetchUsers} from '../../../persistence/requests/fetcher';
import {getUserUsername} from '../../../security/auth';
import AddUserModal from './add-user-modal';
import DeleteUserModal from './delete-user-modal';
import {UserRequest} from '../../../persistence/model/requests';
import {mapUserRequest} from '../../../persistence/mapper/mapper';
import {LongTeam, LongUser} from '../../../persistence/model/data';
import { updateUser } from '../../../persistence/requests/updater';

export interface UserInfo {
    id: number;
    name: string;
}

export default function Users() {
    const [users, setUsers] = useState<LongUser[]>([]);
    const [showAddUserModal, setShowAddUserModal] = useState(false);
    const [showDeleteUserConfirmation, setShowDeleteUserConfirmation] = useState(false);
    const [userToDelete, setUserToDelete] = useState<UserInfo | null>(null);
    const [editUserId, setEditUserId] = useState<number | null>(null);
    const [editUserValues, setEditUserValues] = useState<UserRequest>({});
    const [teams, setTeams] = useState<LongTeam[]>([]);
    const [roles, setRoles] = useState<string[]>([]);

    const handleAddUserShow = () => setShowAddUserModal(true);
    const handleUserDeleteShow = (userId: number, username: string) => {
        setUserToDelete({id: userId, name: username});
        setShowDeleteUserConfirmation(true);
    };
    const handleUserEdit = (user: LongUser) => {
        setEditUserId(user.id);
        setEditUserValues({...mapUserRequest(user)});
    };
    const handleFieldChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
        field: string
    ) => {
        setEditUserValues({...editUserValues, [field]: e.target.value});
    };
    const handleSelectChange = (field: string, team: LongTeam | undefined) => {
        setEditUserValues({...editUserValues, [field]: team ? team.id : null});
    };
    const handleSubmitUserEdit = () => {
        if (editUserId === null) return;
        updateUser(editUserId, editUserValues).then(handleCancelUserEdit);
    };
    const handleCancelUserEdit = () => {
        setEditUserId(null);
    };

    useEffect(() => {
        fetchUsers().then(setUsers);
    }, [showAddUserModal, showDeleteUserConfirmation, !editUserId]);
    useEffect(() => {
        fetchTeams().then(setTeams);
    }, []);
    useEffect(() => {
        fetchRoles().then(setRoles);
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
                    <th>Role</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map((user) => (
                    <tr key={user.id} className="align-middle">
                        {editUserId === user.id ? (
                            <>
                                <td>
                                    <Form.Control
                                        type="text"
                                        value={editUserValues.firstName}
                                        onChange={(e) => handleFieldChange(e, 'firstName')}
                                    />
                                    <Form.Control
                                        type="text"
                                        value={editUserValues.lastName}
                                        onChange={(e) => handleFieldChange(e, 'lastName')}
                                    />
                                </td>
                                <td>
                                    <Form.Control
                                        type="datalist"
                                        value={editUserValues.username}
                                        onChange={(e) => handleFieldChange(e, 'username')}
                                    />
                                </td>
                                <td>
                                    <Form.Select
                                        defaultValue={
                                            editUserValues.teamId !== null && editUserValues.teamId !== undefined
                                                ? editUserValues.teamId
                                                : undefined
                                        }
                                        onChange={(e) =>
                                            handleSelectChange(
                                                'teamId',
                                                teams.find((t) => t.id === Number(e.target.value))
                                            )
                                        }
                                    >
                                        <option value={undefined}>-</option>
                                        {teams.map((team) => (
                                            <option value={team.id} key="team-id">
                                                {team.name}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </td>
                                <td>
                                    <Form.Select
                                        defaultValue={editUserValues.role}
                                        onChange={(e) => handleFieldChange(e, 'role')}
                                    >
                                        {roles.map((role) => (
                                            <option value={role} key={role}>
                                                {role}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </td>
                                <td>
                                    <Button variant="success" size="sm" onClick={handleSubmitUserEdit}>
                                        <Check/>
                                    </Button>
                                    <Button variant="outline-danger" size="sm" onClick={handleCancelUserEdit}>
                                        <X/>
                                    </Button>
                                </td>
                            </>
                        ) : (
                            <>
                                <td>
                                    {user.firstName} {user.lastName}
                                </td>
                                <td>
                                    {user.username}
                                    <b>{getUserUsername() === user.username ? ' (you)' : ''}</b>
                                </td>
                                <td>{user.team ? user.team.name : '-'}</td>
                                <td>{user.role}</td>
                                <td>
                                    <Button
                                        variant="warning"
                                        size="sm"
                                        className="me-2"
                                        onClick={() => handleUserEdit(user)}
                                    >
                                        <Pencil/>
                                    </Button>
                                    <Button
                                        variant="danger"
                                        size="sm"
                                        className="me-2"
                                        onClick={() => handleUserDeleteShow(user.id, user.username)}
                                    >
                                        <Trash/>
                                    </Button>
                                </td>
                            </>
                        )}
                    </tr>
                ))}
                </tbody>
            </Table>

            <Button variant="primary" className="mb-3" onClick={handleAddUserShow}>
                <Plus/> Add User
            </Button>

            <AddUserModal showAddUserModal={showAddUserModal} setShowAddUserModal={setShowAddUserModal}/>

            <DeleteUserModal
                showDeleteUserConfirmation={showDeleteUserConfirmation}
                setShowDeleteUserConfirmation={setShowDeleteUserConfirmation}
                userToDelete={userToDelete}
                setUserToDelete={setUserToDelete}
            />
        </div>
    );
}
