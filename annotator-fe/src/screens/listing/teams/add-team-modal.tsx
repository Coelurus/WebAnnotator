import React from 'react';
import Button from 'react-bootstrap/esm/Button';
import Form from 'react-bootstrap/esm/Form';

import Modal from 'react-bootstrap/esm/Modal';
import { TeamRequest } from '../../../persistence/model/requests';
import { createTeamRequest } from '../../../persistence/requests/poster';
import { LongUser } from '../../../persistence/model/data';
import { fetchUsers } from '../../../persistence/requests/fetcher';

interface AddTeamModalProps {
  showAddTeamModal: boolean;
  setShowAddTeamModal: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function AddTeamModal({ showAddTeamModal, setShowAddTeamModal }: AddTeamModalProps) {
  const [newTeam, setNewTeam] = React.useState<TeamRequest>({});
  const [users, setUsers] = React.useState<LongUser[]>([]);

  React.useEffect(() => {
      fetchUsers().then(setUsers);
    }, []);

  const handleAddTeamClose = () => setShowAddTeamModal(false);
  const handleAddTeamSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    createTeamRequest(newTeam).then(() => {
        setNewTeam({});
        handleAddTeamClose();
    });
    
  };
  const handleAddTeamChange = (value: string, name: string) => {
    setNewTeam({ ...newTeam, [name]: value });
  };
  const handleSelectChange = (field: string, user: LongUser | undefined) => {
    setNewTeam({ ...newTeam, [field]: user ? user.id : null });
    };

  return (
    <Modal show={showAddTeamModal} onHide={handleAddTeamClose}>
        <Modal.Header closeButton>
        <Modal.Title>Add User</Modal.Title>
        </Modal.Header>
        <Modal.Body>
        <Form onSubmit={(e) => handleAddTeamSubmit(e)}>
            <Form.Group className="mb-3">
            <Form.Label>Team Name</Form.Label>
            <Form.Control
                type="text"
                name="name"
                onChange={(e) => handleAddTeamChange(e.target.value, e.target.name)}
                required
            />
            </Form.Group>
            <Form.Group className="mb-3">
            <Form.Label>Team Leader</Form.Label>
                <Form.Select
                    defaultValue={users[0]?.id}
                    onChange={(e) =>
                    handleSelectChange(
                        'leaderId',
                        users.find((u) => u.id === Number(e.target.value))
                    )
                    }
                >
                    {users.map((user) => (
                    <option value={user.id} key={`leader-id-${user.id}`}>
                        {user.firstName} {user.lastName}
                    </option>
                    ))}
                </Form.Select>
            </Form.Group>
            <Button variant="primary" type="submit">
            Add Team
            </Button>
        </Form>
        </Modal.Body>
    </Modal>
  );
}
