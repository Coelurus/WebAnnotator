import {
  TeamResponse,
  UserResponse,
  ProjectResponse,
  PriorityResponse
} from '../../persistence/model/responses';
import {
  mapTeamResponse,
  mapUserResponse,
  mapProjectResponse,
  mapPriorityResponse
} from '../../persistence/mapper/mapper';
import { request } from '../../security/auth';

export async function fetchTeams(): Promise<TeamResponse[]> {
  try {
    const response = await request('GET', '/api/teams');
    return mapTeamResponse(response.data);
  } catch (error) {
    console.error('Error fetching teams:', error);
    return [];
  }
}

export async function fetchUsers(): Promise<UserResponse[]> {
  try {
    const response = await request('GET', '/api/users');
    return mapUserResponse(response.data);
  } catch (error) {
    console.error('Error fetching users:', error);
    return [];
  }
}

export async function fetchPriorities(): Promise<PriorityResponse[]> {
  try {
    const response = await request('GET', '/api/priorities');
    return mapPriorityResponse(response.data);
  } catch (error) {
    console.error('Error fetching priorities:', error);
    return [];
  }
}

export async function fetchProject(id: number): Promise<ProjectResponse | null> {
  try {
    const response = await request('GET', `/api/projects/${id}`);
    return mapProjectResponse(response.data);
  } catch (error) {
    console.error('Error fetching project:', error);
    return null;
  }
}
