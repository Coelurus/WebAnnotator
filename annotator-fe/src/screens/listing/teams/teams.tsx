import React from 'react';

import {fetchTeams, fetchUsers} from '../../../persistence/requests/fetcher';
import {Button, Form, Modal, OverlayTrigger, Table, Tooltip} from 'react-bootstrap';
import {Ban, Check, InfoCircle, Pencil, PersonAdd, Plus, Trash, X} from 'react-bootstrap-icons';
import {deleteTeamRequest} from '../../../persistence/requests/deleter';
import {LongTeam, LongUser} from '../../../persistence/model/data';
import AddTeamModal from './add-team-modal';
import {TeamRequest} from '../../../persistence/model/requests';
import {mapTeamRequest} from '../../../persistence/mapper/mapper';
import {request} from '../../../security/auth';
import AddMemberModal from './add-team-member-modal';

interface TeamInfo {
    id: number;
    name: string;
}

export default function Teams() {
    const [users, setUsers] = React.useState<LongUser[]>([]);
    const [teams, setTeams] = React.useState<LongTeam[]>([]);
    const [showAddTeamModal, setShowAddTeamModal] = React.useState(false);
    const [teamToDelete, setTeamToDelete] = React.useState<TeamInfo | null>(null);
    const [showDeleteTeamConfirmation, setShowDeleteTeamConfirmation] = React.useState(false);
    const [editTeamId, setEditTeamId] = React.useState<number | null>(null);
    const [editTeamValues, setEditTeamValues] = React.useState<TeamRequest>({});
    const [showAddMemberModal, setShowAddMemberModal] = React.useState(false);
    const [teamToAddMemberTo, setTeamToAddMemberTo] = React.useState<LongTeam | null>();

    const handleShow = () => setShowAddTeamModal(true);

    React.useEffect(() => {
        fetchTeams().then(setTeams);
    }, [!showDeleteTeamConfirmation, !teamToDelete, !showAddTeamModal, editTeamId === null]);

    React.useEffect(() => {
        fetchUsers().then(setUsers);
    }, [!showAddTeamModal, !showAddMemberModal]);

    const handleTeamEdit = (team: LongTeam) => {
        setEditTeamId(team.id);
        setEditTeamValues({...mapTeamRequest(team)});
    };
    const handleTeamFieldChange = (
        field: string,
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) => {
        setEditTeamValues({...editTeamValues, [field]: e.target.value});
    };
    const handleSubmitTeamEdit = () => {
        request('PUT', `./api/teams/${editTeamId}`, editTeamValues).then(() => handleCancelTeamEdit());
    };
    const handleCancelTeamEdit = () => {
        setEditTeamId(null);
    };

    const handleTeamDelete = (teamId: number, teamName: string) => {
        setTeamToDelete({id: teamId, name: teamName});
        setShowDeleteTeamConfirmation(true);
    };

    const handleDeleteTeamClose = () => setShowDeleteTeamConfirmation(false);

    const handleAddTeamMember = (team: LongTeam) => {
        setShowAddMemberModal(true);
        setTeamToAddMemberTo(team);
    };

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

            <AddTeamModal showAddTeamModal={showAddTeamModal} setShowAddTeamModal={setShowAddTeamModal}/>

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
