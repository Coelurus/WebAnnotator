import React, { useState, useEffect, FormEvent } from "react";
import { Table, Button, Modal, Form } from "react-bootstrap";
import { Pencil, Plus, Trash } from "react-bootstrap-icons";
import { UserResponse } from "../../persistence/model/responses";
import { fetchUsers } from "../../persistence/fetcher/fetcher";

export default function Users() {
    const [users, setUsers] = useState<UserResponse[]>([]);
    const [show, setShow] = useState(false);
    const [newUser, setNewUser] = useState({ firstName: "", lastName: "", username: "" });

    const handleShow = () => setShow(true);
    const handleClose = () => setShow(false);
    const handleChange = (value: string, name: string) => {
        setNewUser({ ...newUser, [name]: value });
    };
    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        alert("Here should happen smth like : handleUserAdd(newUser)");
        setNewUser({ firstName: "", lastName: "", username: "" });
        handleClose();
    };

    useEffect(() => {
        fetchUsers().then(setUsers);
    }, []);

    const handleUserEdit = (userId: number) => {
        alert("TODO: Edit user: " + userId);
    };

    const handleUserDelete = (userId: number) => {
        alert("TODO: Delete user: " + userId);
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
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((user) => (
                        <tr key={user.id} className="align-middle">
                            <td>{user.firstName} {user.lastName}</td>
                            <td>{user.username}</td>
                            <td>{user.team ? user.team.name : "-"}</td>
                            <td>
                                <Button 
                                    variant="warning" 
                                    className="me-2" 
                                    size="sm"
                                    onClick={() => handleUserEdit(user.id)}
                                >
                                    <Pencil />
                                </Button>
                                <Button 
                                    variant="danger" 
                                    size="sm"
                                    onClick={() => handleUserDelete(user.id)}
                                >
                                    <Trash />
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            <Button variant="primary" className="mb-3" onClick={handleShow}>
                <Plus /> Add User
            </Button>
            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Add User</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={(e) => handleSubmit(e)}>
                        <Form.Group className="mb-3">
                            <Form.Label>First Name</Form.Label>
                            <Form.Control type="text" name="firstName" value={newUser.firstName} onChange={(e) => handleChange(e.target.value, e.target.name)} required />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Last Name</Form.Label>
                            <Form.Control type="text" name="lastName" value={newUser.lastName} onChange={(e) => handleChange(e.target.value, e.target.name)} required />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Username</Form.Label>
                            <Form.Control type="text" name="username" value={newUser.username} onChange={(e) => handleChange(e.target.value, e.target.name)} required />
                        </Form.Group>
                        <Button variant="primary" type="submit">
                            Add User
                        </Button>
                    </Form>
                </Modal.Body>
            </Modal>
        </div>
    );
}