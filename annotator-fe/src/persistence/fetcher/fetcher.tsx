import { TeamResponse, UserResponse, ProjectResponse, PriorityResponse } from "../../persistence/model/responses";
import { mapTeamResponse, mapUserResponse, mapProjectResponse,  mapPriorityResponse} from "../../persistence/mapper/mapper";

export async function fetchTeams(): Promise<TeamResponse[]> {
    try {
        const response = await fetch('/api/teams');
        if(!response.ok) {
            throw new Error('Network response was not ok.')
        }
        const data = await response.json();
        return mapTeamResponse(data);
    } catch (error) {
        console.error('Error fetching teams:', error);
        return [];
    }
}

export async function fetchPriorities(): Promise<PriorityResponse[]> {
    try {
        const response = await fetch('/api/priorities');
        if(!response.ok) {
            throw new Error('Network response was not ok.')
        }
        const data = await response.json();
        return mapPriorityResponse(data);
    } catch (error) {
        console.error('Error fetching priorities:', error);
        return [];
    }
}

export async function fetchProject(id: number): Promise<ProjectResponse | null> {
    try {
        const response = await fetch('/api/projects/' + id);
        if(!response.ok) {
            throw new Error('Network response was not ok.')
        }
        const data = await response.json();
        return mapProjectResponse(data);
    } catch (error) {        
        console.error('Error fetching project:', error);
        return null;
    }
}