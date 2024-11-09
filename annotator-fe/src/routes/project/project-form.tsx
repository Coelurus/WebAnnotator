import { useEffect, useState } from "react";

import { PriorityResponse, TeamResponse } from "../../persistence/model/responses";
import { fetchPriorities, fetchTeams } from "../../persistence/fetcher/fetcher";

export default function ProjectForm() {

    const [teams, setTeams] = useState<TeamResponse[]>([]);
    useEffect(() => {
        fetchTeams().then(setTeams);
    }, []);

    const [priorities, setPriorities] = useState<PriorityResponse[]>([]);
    useEffect(() => {
        fetchPriorities().then(setPriorities);
    }, []);

    return (
        <form method="post" action="/api/projects/upload" encType="multipart/form-data">
            <table>
                <tbody>
                    <tr>
                        <td>Project name:</td>
                        <td><input type="text" name="project_name" required /></td>
                    </tr>
                    <tr>
                        <td>File to upload:</td>
                        <td><input type="file" name="file" /></td>
                    </tr>
                    <tr>
                        <td>Deadline:</td>
                        <td><input type="date" name="deadline" /></td>
                    </tr>
                    <tr>
                        <td>Priority:</td>
                        <td>
                            <select name="priority" required>
                                <option value="" key="priority_none">None</option>
                                {priorities.map((priority) => (
                                    <option value={priority.name} key={"priority_" + priority.name}>
                                        {priority.name}
                                    </option>
                                ))}
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>Team:</td>
                        <td>
                            <select name="team_id" required>
                                <option value="" key="team_all">All</option>
                                {teams.map((team) => (
                                    <option value={team.id} key={"team_" + team.id}>
                                        {team.name}
                                    </option>
                                ))}
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><input type="submit" value="Upload" /></td>
                    </tr>
                </tbody>
            </table>
        </form>
    );
}