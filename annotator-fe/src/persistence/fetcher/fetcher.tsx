import { LongTeam, LongUser, Project, Priority } from '../model/data';
import {
  mapTeamResponse,
  mapUserResponse,
  mapProjectResponse,
  mapPriorityResponse,
  mapRoles
} from '../../persistence/mapper/mapper';
import { request } from '../../security/auth';

export async function fetchTeams(): Promise<LongTeam[]> {
  try {
    const response = await request('GET', '/api/teams');
    return mapTeamResponse(response.data);
  } catch (error) {
    console.error('Error fetching teams:', error);
    return [];
  }
}

export async function fetchUsers(): Promise<LongUser[]> {
  try {
    const response = await request('GET', '/api/users');
    return mapUserResponse(response.data);
  } catch (error) {
    console.error('Error fetching users:', error);
    return [];
  }
}

export async function fetchPriorities(): Promise<Priority[]> {
  try {
    const response = await request('GET', '/api/priorities');
    return mapPriorityResponse(response.data);
  } catch (error) {
    console.error('Error fetching priorities:', error);
    return [];
  }
}

export async function fetchProject(id: number): Promise<Project | null> {
  try {
    const response = await request('GET', `/api/projects/${id}`);
    return mapProjectResponse(response.data);
  } catch (error) {
    console.error('Error fetching project:', error);
    return null;
  }
}

export async function fetchRoles(): Promise<string[]> {
  try {
    const response = await request('GET', '/api/users/roles');
    return mapRoles(response.data);
  } catch (error) {
    console.error('Error fetching roles:', error);
    return [];
  }
}
