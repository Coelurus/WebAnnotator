import React from 'react';
import { X } from 'react-bootstrap-icons';
import Button from 'react-bootstrap/esm/Button';
import Modal from 'react-bootstrap/esm/Modal';
import { getUserUsername, invalidateToken } from '../../../security/auth';
import { UserInfo } from './users';
import { deleteUserRequest } from '../../../persistence/requests/deleter';

/**
 * Interface for the properties of the DeleteUserModal component.
 */
export interface DeleteUserModalProps {
  /**
   * Boolean to control the visibility of the modal.
   */
  showDeleteUserConfirmation: boolean;
  /**
   * Function to set the visibility state of the modal.
   */
  setShowDeleteUserConfirmation: React.Dispatch<React.SetStateAction<boolean>>;
  /**
   * The user to be deleted.
   */
  userToDelete: UserInfo | null;
  /**
   * Function to set the user to delete.
   */
  setUserToDelete: React.Dispatch<React.SetStateAction<UserInfo | null>>;
}

/**
 * Component for confirming the deletion of a user through a modal.
 *
 * @param props The properties for the DeleteUserModal component.
 * @returns JSX Element representing the delete user confirmation modal.
 */
export default function DeleteUserModal({
  showDeleteUserConfirmation,
  setShowDeleteUserConfirmation,
  userToDelete,
  setUserToDelete
}: DeleteUserModalProps) {

  /**
   * Function to handle the deletion of a user.
   */
  const deleteUser = async () => {
    if (userToDelete) {
      deleteUserRequest(userToDelete.id)
      setUserToDelete(null);
      // If user deleted themself, invalidate their token
      if (getUserUsername() === userToDelete.name) {
        invalidateToken();
      }
      handleDeleteUserClose();
    }
  };

  /**
   * Function to close the delete user confirmation modal.
   */
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
