import React from "react";
import Form from "react-bootstrap/esm/Form";
import Modal from "react-bootstrap/esm/Modal";
import { LongTeam, LongUser } from "../../../persistence/model/data";
import { addTeamMember } from "../../../persistence/requests/poster";
import Button from "react-bootstrap/esm/Button";

interface AddMemberModalProps {
  showAddMemberModal: boolean;
  setShowAddMemberModal: React.Dispatch<React.SetStateAction<boolean>>;
  users: LongUser[],
  teamToAddMemberTo: LongTeam | null | undefined
}

export default function AddMemberModal({ showAddMemberModal, setShowAddMemberModal, users, teamToAddMemberTo }: AddMemberModalProps) {
  const [selectedUser, setSelectedUser] = React.useState<LongUser | null>();

  const handleAddMemberClose = () => setShowAddMemberModal(false);

  const handleAddMemberSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if(!teamToAddMemberTo || !selectedUser) {
        setSelectedUser(null)
        handleAddMemberClose()
        return
    }
    addTeamMember(selectedUser.id, teamToAddMemberTo.id)
        .then(() => {
            setSelectedUser(null)
            handleAddMemberClose()
        })
  }

  const handleTeamMemberChange = (user: LongUser|undefined) => {
    if(user){
        setSelectedUser(user)
    }
  }

  return (
    <Modal show={showAddMemberModal} onHide={handleAddMemberClose}>
        <Modal.Header closeButton>
        <Modal.Title>Add User</Modal.Title>
        </Modal.Header>
        <Modal.Body>
        <Form onSubmit={(e) => handleAddMemberSubmit(e)}>
            <Form.Group className="mb-3">
            <Form.Label>Select user to add</Form.Label>
                <Form.Select
                    defaultValue={users[0]?.id}
                    onChange={(e) =>
                    handleTeamMemberChange(
                        users.find((u) => u.id === Number(e.target.value))
                    )
                    }
                >
                    {users.filter(u => u.team?.id !== teamToAddMemberTo?.id).map((user) => (
                    <option value={user.id} key={`leader-id-${user.id}`}>
                        {user.firstName} {user.lastName}
                    </option>
                    ))}
                </Form.Select>
            </Form.Group>
            <Button variant="primary" type="submit">
                Add Member
            </Button>
        </Form>
        </Modal.Body>
    </Modal>
  );
}
