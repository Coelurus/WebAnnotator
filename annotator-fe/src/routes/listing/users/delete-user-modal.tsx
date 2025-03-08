import React from 'react';
import { X } from 'react-bootstrap-icons';
import Button from 'react-bootstrap/esm/Button';
import Modal from 'react-bootstrap/esm/Modal';
import { getUserUsername, invalidateToken, request } from '../../../security/auth';
import { UserInfo } from './users';

interface DeleteUserModalProps {
  showDeleteUserConfirmation: boolean;
  setShowDeleteUserConfirmation: React.Dispatch<React.SetStateAction<boolean>>;
  userToDelete: UserInfo | null;
  setUserToDelete: React.Dispatch<React.SetStateAction<UserInfo | null>>;
}

export default function DeleteUserModal({
  showDeleteUserConfirmation,
  setShowDeleteUserConfirmation,
  userToDelete,
  setUserToDelete
}: DeleteUserModalProps) {
  const deleteUser = async () => {
    if (userToDelete) {
      await request('DELETE', `/api/users/${userToDelete.id}`);
      setUserToDelete(null);
      setShowDeleteUserConfirmation(false);
      if (getUserUsername() === userToDelete.name) {
        invalidateToken();
      }
      window.location.reload();
    }
  };

  const handleDeleteUserClose = () => setShowDeleteUserConfirmation(false);

  return (
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
  );
}
