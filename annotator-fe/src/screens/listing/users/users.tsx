import React from 'react';
import {Button, Form, OverlayTrigger, Table, Tooltip} from 'react-bootstrap';
import {Check, Pencil, Plus, Trash, X} from 'react-bootstrap-icons';
import {fetchRoles, fetchTeams, fetchUsers} from '../../../persistence/requests/fetcher';
import {getUserUsername} from '../../../security/auth';
import AddUserModal from './add-user-modal';
import DeleteUserModal from './delete-user-modal';
import {UserRequest} from '../../../persistence/model/requests';
import {mapUserRequest} from '../../../persistence/mapper/mapper';
import {LongTeam, LongUser} from '../../../persistence/model/data';
import { updateUser } from '../../../persistence/requests/updater';

/**
 * Interface to identify a user for deletion confirmation.
 */
export interface UserInfo {
    /**
     * ID of the user to be deleted.
     */
    id: number;
    /**
     * Name of the user to be deleted.
     */
    name: string;
}

/**
 * Users component displays a list of users with options to add, edit, and delete users.
 * It fetches users, teams, and roles from the server and allows for user management.
 * 
 * @returns JSX Element representing the users listing screen.
 */
export default function Users() {
    // State to manage the list of users
    const [users, setUsers] = React.useState<LongUser[]>([]);
    // State to manage the list of teams
    const [teams, setTeams] = React.useState<LongTeam[]>([]);
    // State to manage the list of roles
    const [roles, setRoles] = React.useState<string[]>([]);
    // State to manage the visibility of the add user modal
    const [showAddUserModal, setShowAddUserModal] = React.useState(false);
    // State to manage the visibility of the delete user confirmation modal
    const [showDeleteUserConfirmation, setShowDeleteUserConfirmation] = React.useState(false);
    // State to manage the user to be deleted
    const [userToDelete, setUserToDelete] = React.useState<UserInfo | null>(null);
    // State to manage the user being edited
    const [editUserId, setEditUserId] = React.useState<number | null>(null);
    // State to manage the values of the user being edited
    const [editUserValues, setEditUserValues] = React.useState<UserRequest>({});
    
    // Effect to fetch teams and roles when the component mounts
    React.useEffect(() => {
        fetchTeams().then(setTeams);
        fetchRoles().then(setRoles);
    }, []);
    
    // Effect to fetch users when the component mounts or when modals are shown/hidden
    React.useEffect(() => {
        fetchUsers().then(setUsers);
    }, [showAddUserModal, showDeleteUserConfirmation, !editUserId]);

    /**
     * Function to handle showing the add user modal.
     */
    const handleAddUserShow = () => setShowAddUserModal(true);

    /**
     * Function to handle showing the delete user confirmation modal on clicking the delete button.
     * 
     * @param userId ID of the user to delete
     * @param username Username of the user to delete
     */
    const handleUserDeleteShow = (userId: number, username: string) => {
        setUserToDelete({id: userId, name: username});
        setShowDeleteUserConfirmation(true);
    };

    /**
     * Function to handle editing a user when the edit button is clicked.
     * 
     * @param user User object to edit
     */
    const handleUserEdit = (user: LongUser) => {
        setEditUserId(user.id);
        setEditUserValues({...mapUserRequest(user)});
    };

    /**
     * Function to handle changes in the input fields of the user edit form.
     * 
     * @param e Change event from the input field
     * @param field Name of the field being changed
     */
    const handleFieldChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
        field: string
    ) => {
        setEditUserValues({...editUserValues, [field]: e.target.value});
    };

    /**
     * Function to handle changes in the select dropdowns for team and role.
     * 
     * @param field Name of the field being changed
     * @param team Selected team object or undefined if no team is selected
     */
    const handleSelectChange = (field: string, team: LongTeam | undefined) => {
        setEditUserValues({...editUserValues, [field]: team ? team.id : null});
    };

    /**
     * Function to handle submitting the edited user information.
     */
    const handleSubmitUserEdit = () => {
        if (editUserId === null) return;
        updateUser(editUserId, editUserValues).then(handleCancelUserEdit);
    };

    /**
     * Function to handle canceling the user edit.
     */
    const handleCancelUserEdit = () => {
        setEditUserId(null);
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
                                    <OverlayTrigger
                                        placement="top"
                                        overlay={<Tooltip id={`tooltip-edit-user-${user.id}`}>Edit User</Tooltip>}
                                    >
                                        <Button
                                            variant="warning"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleUserEdit(user)}
                                        >
                                            <Pencil/>
                                        </Button>
                                    </OverlayTrigger>

                                    <OverlayTrigger
                                        placement="top"
                                        overlay={<Tooltip id={`tooltip-delete-user-${user.id}`}>Delete User</Tooltip>}
                                    >
                                        <Button
                                            variant="danger"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleUserDeleteShow(user.id, user.username)}
                                        >
                                            <Trash/>
                                        </Button>
                                    </OverlayTrigger>

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
