import { LongTeam, LongUser, Project, Priority, Label, Annotation } from '../model/data';
import {
  mapTeamResponse,
  mapUserResponse,
  mapProjectResponse,
  mapPriorityResponse,
  mapRoles,
  mapLabels,
  mapFrameCount,
  mapAnnotations,
  mapProjectResponses
} from '../mapper/mapper';
import { request } from '../../security/auth';

export function fetchTeams(): Promise<LongTeam[]> {
  return request('GET', '/api/teams')
    .then((response) => mapTeamResponse(response.data))
    .catch((error) => {
      console.error('Error fetching teams:', error);
      return [];
    });
}

export function fetchUsers(): Promise<LongUser[]> {
  return request('GET', '/api/users')
    .then((response) => mapUserResponse(response.data))
    .catch((error) => {
      console.error('Error fetching users:', error);
      return [];
    });
}

export function fetchPriorities(): Promise<Priority[]> {
  return request('GET', '/api/priorities')
    .then((response) => mapPriorityResponse(response.data))
    .catch((error) => {
      console.error('Error fetching priorities:', error);
      return [];
    });
}

export function fetchProjects(onError: (message: string) => void): Promise<Project[]> {
  return request('GET', '/api/projects')
    .then((response) => mapProjectResponses(response.data))
    .catch((error) => {
      console.error(error);
      onError('Error fetching projects');
      return [];
    });
}

export function fetchProject(id: number): Promise<Project | null> {
  return request('GET', `/api/projects/${id}`)
    .then((response) => mapProjectResponse(response.data))
    .catch((error) => {
      console.error('Error fetching project:', error);
      return null;
    });
}

export function fetchRoles(): Promise<string[]> {
  return request('GET', '/api/users/roles')
    .then((response) => mapRoles(response.data))
    .catch((error) => {
      console.error('Error fetching roles:', error);
      return [];
    });
}

export function fetchLabels(onError: (message: string) => void): Promise<Label[]> {
  return request('GET', '/api/labels')
    .then((response) => mapLabels(response.data))
    .catch((error) => {
      console.error(error);
      onError('Error fetching labels');
      return [];
    });
}

export function fetchFrameCount(
  projectId: number,
  onError: (message: string) => void
): Promise<number> {
  return request('GET', `/api/projects/${projectId}/frame/count`)
    .then((response) => mapFrameCount(response.data) )
    .catch((error) => {
      console.error(error);
      onError('Error fetching frame count');
      return 0;
    });
}

export function fetchAnnotations(
  projectId: number,
  onError: (message: string) => void
): Promise<Annotation[]> {
  return request('GET', `/api/projects/${projectId}/annotations`)
    .then((response) => mapAnnotations(response.data))
    .catch((error) => {
      console.error(error);
      onError('Error fetching annotations');
      return [];
    });
}