import React, { FormEvent, useEffect, useState } from 'react';

import { fetchTeams } from '../../../persistence/requests/fetcher';
import { Button, Form, Modal, Table } from 'react-bootstrap';
import { Pencil, PersonAdd, Plus, Trash, X } from 'react-bootstrap-icons';
import { LongTeamApiResponse } from '../../../persistence/model/api-responses';
import { deleteTeamRequest } from '../../../persistence/requests/deleter';

interface TeamInfo {
  id: number;
  name: string;
}

export default function Teams() {
  const [teams, setTeams] = useState<LongTeamApiResponse[]>([]);
  const [show, setShow] = useState(false);
  const [newTeam, setNewTeam] = useState({ teamName: '', leaderName: '' });
  const [teamToDelete, setTeamToDelete] = useState<TeamInfo | null>(null);
  const [showDeleteTeamConfirmation, setShowDeleteTeamConfirmation] = useState(false);

  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);
  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    alert('Here should happen smth like : handleTeamAdd(newTeam)');
    setNewTeam({ teamName: '', leaderName: '' });
    handleClose();
  };
  const handleChange = (value: string, name: string) => {
    setNewTeam({ ...newTeam, [name]: value });
  };

  useEffect(() => {
    fetchTeams().then(setTeams);
  }, []);

  const handleTeamEdit = (teamId: number) => {
    alert('TODO: Edit team: ' + teamId);
  };

  const handleTeamDelete = (teamId: number, teamName: string) => {
    setTeamToDelete({ id: teamId, name: teamName });
    setShowDeleteTeamConfirmation(true);
  };

  const handleDeleteTeamClose = () => setShowDeleteTeamConfirmation(false);

  const handleAddTeamMember = (teamId: number) => {
    alert('TODO: Add teammember to team: ' + teamId);
  };

  const deleteTeam = async () => {
    deleteTeamRequest(teamToDelete?.id ?? 0, (message) =>
      setToastMessage({ header: 'Error', body: message, variant: 'danger' })
    );
    setTeamToDelete(null);
    window.location.reload();
  };

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Teams</h1>
      <Table striped bordered hover responsive>
        <thead className="table-dark">
          <tr>
            <th>Name</th>
            <th>Leader</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {teams.map((team) => (
            <tr key={team.id}>
              <td>{team.name}</td>
              <td>{team.leader ? team.leader.firstName + ' ' + team.leader.lastName : '-'}</td>
              <td>
                <Button
                  variant="success"
                  size="sm"
                  className="me-2"
                  onClick={() => handleAddTeamMember(team.id)}
                >
                  <PersonAdd />
                </Button>
                <Button
                  variant="warning"
                  className="me-2"
                  size="sm"
                  onClick={() => handleTeamEdit(team.id)}
                >
                  <Pencil />
                </Button>
                <Button
                  variant="danger"
                  size="sm"
                  className="me-2"
                  onClick={() => handleTeamDelete(team.id, team.name)}
                >
                  <Trash />
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <Button variant="primary" className="mb-3" onClick={handleShow}>
        <Plus /> Add Team
      </Button>
      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Add User</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={(e) => handleSubmit(e)}>
            <Form.Group className="mb-3">
              <Form.Label>Team Name</Form.Label>
              <Form.Control
                type="text"
                name="teamName"
                value={newTeam.teamName}
                onChange={(e) => handleChange(e.target.value, e.target.name)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Team Leader</Form.Label>
              <Form.Control
                type="text"
                name="leaderName"
                value={newTeam.leaderName}
                onChange={(e) => handleChange(e.target.value, e.target.name)}
                required
              />
            </Form.Group>
            <Button variant="primary" type="submit">
              Add Team
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={showDeleteTeamConfirmation} onHide={handleDeleteTeamClose}>
        <Modal.Header closeButton>
          <Modal.Title>Do you really want to delete team?</Modal.Title>
        </Modal.Header>
        <Modal.Body className="d-flex justify-content-around">
          <Button variant="success" className="mb-3 me-5" onClick={handleDeleteTeamClose}>
            <X /> DO NOT DELETE
          </Button>
          <Button variant="danger" className="mb-3" onClick={deleteTeam}>
            <X /> DELETE
          </Button>
        </Modal.Body>
      </Modal>
    </div>
  );
}
