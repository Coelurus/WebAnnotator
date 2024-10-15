import { useEffect, useState } from "react";

import { TeamResponse } from "../../persistence/model/responses";
import { fetchTeams } from "../../persistence/fetcher/fetcher";


export default function Teams() {
    const [teams, setTeams] = useState<TeamResponse[]>([]);

    useEffect(() => {
        fetchTeams().then(setTeams);
    }, []);    

    return (
        <div>
          <h1>Teams</h1>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Leader</th>
              </tr>
            </thead>
            <tbody>
              {teams.map((team) => (
                <tr key={team.id}>
                  <td>{team.id}</td>
                  <td>{team.name}</td>
                  <td>{team.leader.firstName} {team.leader.lastName}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      );
}