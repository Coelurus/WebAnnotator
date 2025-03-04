import { useEffect, useState } from "react";

import { UserResponse } from "../../persistence/model/responses";
import { fetchUsers } from "../../persistence/fetcher/fetcher";

export default function Users() {
    const [users, setUsers] = useState<UserResponse[]>([]);

    useEffect(() => {
        fetchUsers().then((data) => {
          setUsers(data)
          console.log(data);
          
        })
    }, []);    


    return (
        <div>
          <h1>Users</h1>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Username</th>
                <th>Team</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.firstName} {user.lastName}</td>
                  <td>{user.username}</td>
                  <td>{user.team ? user.team.name : ""}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
    );
}