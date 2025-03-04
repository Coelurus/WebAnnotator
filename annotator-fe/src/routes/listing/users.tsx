import { useState, useEffect } from "react";
import { Table, Button } from "react-bootstrap";
import { Pencil, Trash } from "react-bootstrap-icons";
import { UserResponse } from "../../persistence/model/responses";
import { fetchUsers } from "../../persistence/fetcher/fetcher";

export default function Users() {
    const [users, setUsers] = useState<UserResponse[]>([]);

    useEffect(() => {
        fetchUsers().then((data) => {
            setUsers(data);
            console.log(data);
        });
    }, []);

    const handleEdit = (userId: number) => {
        alert("TODO: Edit user: " + userId);
    };

    const handleDelete = (userId: number) => {
        alert("TODO: Delete user: " + userId);
    };

    return (
        <div className="container mt-4">
            <h1 className="mb-4">Users</h1>
            <Table striped bordered hover responsive>
                <thead className="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Username</th>
                        <th>Team</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((user) => (
                        <tr key={user.id} className="align-middle">
                            <td>{user.id}</td>
                            <td>{user.firstName} {user.lastName}</td>
                            <td>{user.username}</td>
                            <td>{user.team ? user.team.name : "-"}</td>
                            <td>
                                <Button 
                                    variant="warning" 
                                    className="me-2" 
                                    size="sm"
                                    onClick={() => handleEdit(user.id)}
                                >
                                    <Pencil />
                                </Button>
                                <Button 
                                    variant="danger" 
                                    size="sm"
                                    onClick={() => handleDelete(user.id)}
                                >
                                    <Trash />
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        </div>
    );
}