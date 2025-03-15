import { LongTeam, LongUser, Project, Priority, Label, Annotation, Progress } from '../model/data';
import {
  mapTeamResponse,
  mapUserResponse,
  mapProjectResponse,
  mapPriorityResponse,
  mapRoles,
  mapLabels,
  mapFrameCount,
  mapAnnotations,
  mapProjectResponses,
  mapProgresses
} from '../mapper/mapper';
import { request } from '../../security/auth';
import { ErrorResponse } from '../errors/error-response';
import generateErrorToasts from '../../screens/notifications/toast-util';

export function fetchTeams(): Promise<LongTeam[]> {
  return request('GET', '/api/teams')
    .then((response) => mapTeamResponse(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return [];
    });
}

export function fetchUsers(): Promise<LongUser[]> {
  return request('GET', '/api/users')
    .then((response) => mapUserResponse(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return [];
    });
}

export function fetchPriorities(): Promise<Priority[]> {
  return request('GET', '/api/priorities')
    .then((response) => mapPriorityResponse(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return [];
    });
}

export function fetchProjects(): Promise<Project[]> {
  return request('GET', '/api/projects')
    .then((response) => mapProjectResponses(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return [];
    });
}

export function fetchProject(id: number): Promise<Project | null> {
  return request('GET', `/api/projects/${id}`)
    .then((response) => mapProjectResponse(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return null;
    });
}

export function fetchRoles(): Promise<string[]> {
  return request('GET', '/api/users/roles')
    .then((response) => mapRoles(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return [];
    });
}

export function fetchLabels(): Promise<Label[]> {
  return request('GET', '/api/labels')
    .then((response) => mapLabels(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return [];
    });
}

export function fetchFrameCount(projectId: number): Promise<number> {
  return request('GET', `/api/projects/${projectId}/frame/count`)
    .then((response) => mapFrameCount(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return 0;
    });
}

export function fetchAnnotations(projectId: number): Promise<Annotation[]> {
  return request('GET', `/api/projects/${projectId}/annotations`)
    .then((response) => mapAnnotations(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return [];
    });
}

export function fetchProgresses(): Promise<Progress[]> {
  return request('GET', '/api/projects/progresses')
    .then((response) => mapProgresses(response.data))
    .catch((error: ErrorResponse) => {
      generateErrorToasts(error);
      return [];
    });
}
