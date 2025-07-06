import React from 'react';

import {fetchTeams, fetchUsers} from '../../../persistence/requests/fetcher';
import {Button, Form, Modal, OverlayTrigger, Table, Tooltip} from 'react-bootstrap';
import {Ban, Check, InfoCircle, Pencil, PersonAdd, Plus, Trash, X} from 'react-bootstrap-icons';
import {deleteTeamRequest} from '../../../persistence/requests/deleter';
import {LongTeam, LongUser} from '../../../persistence/model/data';
import AddTeamModal from './add-team-modal';
import {TeamRequest} from '../../../persistence/model/requests';
import {mapTeamRequest} from '../../../persistence/mapper/mapper';
import AddMemberModal from './add-team-member-modal';
import { updateTeam } from '../../../persistence/requests/updater';

/**
 * Interface to identify a team for deletion.
 */
interface TeamInfo {
    /**
     * ID of the team to be deleted.
     */
    id: number;
    /**
     * Name of the team to be deleted.
     */
    name: string;
}

/**
 * Teams component displays a list of teams with options to add, edit, and delete teams.
 * It also allows adding members to teams and editing team details.
 * It fetches the list of teams and users from the server and manages the state of the teams.
 * 
 * @returns JSX Element representing the teams listing screen.
 */
export default function Teams() {
    // State to manage the list of users
    const [users, setUsers] = React.useState<LongUser[]>([]);
    // State to manage the list of teams
    const [teams, setTeams] = React.useState<LongTeam[]>([]);
    // State to manage the visibility of the add team modal
    const [showAddTeamModal, setShowAddTeamModal] = React.useState(false);
    // State to manage the team to be deleted
    const [teamToDelete, setTeamToDelete] = React.useState<TeamInfo | null>(null);
    // State to manage the visibility of the delete team confirmation modal
    const [showDeleteTeamConfirmation, setShowDeleteTeamConfirmation] = React.useState(false);
    // State to manage the team being edited
    const [editTeamId, setEditTeamId] = React.useState<number | null>(null);
    // State to manage the values of the team being edited
    const [editTeamValues, setEditTeamValues] = React.useState<TeamRequest>({});
    // State to manage the visibility of the add member modal
    const [showAddMemberModal, setShowAddMemberModal] = React.useState(false);
    // State to manage the team to which a member is being added
    const [teamToAddMemberTo, setTeamToAddMemberTo] = React.useState<LongTeam | null>();
    
    // Fetch teams when state changes related to team deletion, addition, or editing
    React.useEffect(() => {
        fetchTeams().then(setTeams);
    }, [!showDeleteTeamConfirmation, !teamToDelete, !showAddTeamModal, editTeamId === null]);
    
    // Fetch users when state changes related to adding a team or adding a member
    React.useEffect(() => {
        fetchUsers().then(setUsers);
    }, [!showAddTeamModal, !showAddMemberModal]);
    
    /**
     * Function to handle showing the modal for adding a new team.
     */
    const handleShow = () => setShowAddTeamModal(true);

    /**
     * Function to handle pushing a team edit button.
     */
    const handleTeamEdit = (team: LongTeam) => {
        setEditTeamId(team.id);
        setEditTeamValues({...mapTeamRequest(team)});
    };

    /**
     * Function to handle changes in the team fields during editing.
     * 
     * @param field The field name to update.
     * @param e The change event from the input field.
     */
    const handleTeamFieldChange = (
        field: string,
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) => {
        setEditTeamValues({...editTeamValues, [field]: e.target.value});
    };

    /**
     * Function to handle the submission of the edited team.
     */
    const handleSubmitTeamEdit = () => {
        if(editTeamId === null) return;
        updateTeam(editTeamId, editTeamValues).then(() => handleCancelTeamEdit());
    };

    /**
     * Function to handle canceling the team edit.
     */
    const handleCancelTeamEdit = () => {
        setEditTeamId(null);
    };

    /**
     * Function to handle pushing delete team button.
     * 
     * @param teamId ID of the team to delete
     * @param teamName Name of the team to delete
     */
    const handleTeamDelete = (teamId: number, teamName: string) => {
        setTeamToDelete({id: teamId, name: teamName});
        setShowDeleteTeamConfirmation(true);
    };

    /**
     * Function to handle closing the delete team confirmation modal.
     */
    const handleDeleteTeamClose = () => setShowDeleteTeamConfirmation(false);

    /**
     * Function to handle pushing the add team member button.
     * 
     * @param team The team to which a member is being added
     */
    const handleAddTeamMember = (team: LongTeam) => {
        setShowAddMemberModal(true);
        setTeamToAddMemberTo(team);
    };

    /**
     * Function to delete a team after confirmation.
     */
    const deleteTeam = async () => {
        deleteTeamRequest(teamToDelete?.id ?? 0).then(() => {
            setTeamToDelete(null);
            setShowDeleteTeamConfirmation(false);
        });
    };

    return (
        <div className="container mt-4">
            <h1 className="mb-4">Teams</h1>
            <Table striped bordered hover responsive>
                <thead className="table-dark">
                <tr>
                    <th>Name</th>
                    <th>Leader</th>
                    <th>Members</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {teams.map((team) => (
                    <tr key={team.id}>
                        {editTeamId === team.id ? (
                            <>
                                <td>
                                    <Form.Control
                                        type="text"
                                        value={editTeamValues.name}
                                        onChange={(e) => handleTeamFieldChange('name', e)}
                                    />
                                </td>
                                <td>
                                    <Form.Select
                                        defaultValue={
                                            team.leader?.id !== null && team.leader?.id !== undefined
                                                ? team.leader?.id
                                                : undefined
                                        }
                                        //value={editTeamValues.leaderId ?? ''}
                                        onChange={(e) => handleTeamFieldChange('leaderId', e)}
                                    >
                                        <option value="">-</option>
                                        {users
                                            .filter((u) => u.team?.id === team.id)
                                            .map((user) => (
                                                <option key={user.id} value={user.id}>
                                                    {user.firstName} {user.lastName}
                                                </option>
                                            ))}
                                    </Form.Select>
                                </td>
                                <td>-</td>
                                <td>
                                    <Button variant="success" size="sm" onClick={handleSubmitTeamEdit}>
                                        <Check/>
                                    </Button>
                                    <Button variant="outline-danger" size="sm" onClick={handleCancelTeamEdit}>
                                        <X/>
                                    </Button>
                                </td>
                            </>
                        ) : (
                            <>
                                <td>{team.name}</td>
                                <td>{team.leader ? team.leader.firstName + ' ' + team.leader.lastName : '-'}</td>
                                <td>
                                    {(() => {
                                        const teamMembers = users.filter((u) => u.team?.id === team.id);
                                        return (
                                            <>
                                                {teamMembers.length}
                                                {teamMembers.length > 0 && (
                                                    <OverlayTrigger
                                                        placement="top"
                                                        overlay={
                                                            <Tooltip id={`tooltip-${team.id}`}>
                                                                {teamMembers.map((u) => (
                                                                    <div key={u.id}>
                                                                        {u.firstName} {u.lastName}
                                                                    </div>
                                                                ))}
                                                            </Tooltip>
                                                        }
                                                    >
                                                        <InfoCircle className="ms-2 text-primary"/>
                                                    </OverlayTrigger>
                                                )}
                                            </>
                                        );
                                    })()}
                                </td>
                                <td>
                                    <OverlayTrigger
                                        placement="top"
                                        overlay={<Tooltip id={`tooltip-${team.id}`}>Add team member</Tooltip>}
                                    >
                                        <Button
                                            variant="success"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleAddTeamMember(team)}
                                        >
                                            <PersonAdd/>
                                        </Button>
                                    </OverlayTrigger>

                                    <OverlayTrigger
                                        placement="top"
                                        overlay={<Tooltip id={`tooltip-${team.id}`}>Edit team</Tooltip>}
                                    >
                                        <Button
                                            variant="warning"
                                            className="me-2"
                                            size="sm"
                                            onClick={() => handleTeamEdit(team)}
                                        >
                                            <Pencil/>
                                        </Button>
                                    </OverlayTrigger>

                                    <OverlayTrigger
                                        placement="top"
                                        overlay={<Tooltip id={`tooltip-${team.id}`}>Delete team</Tooltip>}
                                    >
                                        <Button
                                            variant="danger"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleTeamDelete(team.id, team.name)}
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
            <Button variant="primary" className="mb-3" onClick={handleShow}>
                <Plus/> Add Team
            </Button>

            <AddTeamModal 
                showAddTeamModal={showAddTeamModal} 
                setShowAddTeamModal={setShowAddTeamModal}
                users={users}
                setUsers={setUsers}
            />

            <AddMemberModal
                showAddMemberModal={showAddMemberModal}
                setShowAddMemberModal={setShowAddMemberModal}
                users={users}
                teamToAddMemberTo={teamToAddMemberTo}
            />

            <Modal show={showDeleteTeamConfirmation} onHide={handleDeleteTeamClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Do you really want to delete team?</Modal.Title>
                </Modal.Header>
                <Modal.Body className="d-flex justify-content-around">
                    <Button variant="success" className="mb-3 me-5" onClick={handleDeleteTeamClose}>
                        <Ban/> DO NOT DELETE
                    </Button>
                    <Button variant="danger" className="mb-3" onClick={deleteTeam}>
                        <Trash/> DELETE
                    </Button>
                </Modal.Body>
            </Modal>
        </div>
    );
}
