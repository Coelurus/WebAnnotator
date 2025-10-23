import {Annotation, Label, LongTeam, LongUser, Priority, Progress, Project} from '../model/data';
import {
    mapAnnotations,
    mapFrameCount,
    mapLabels,
    mapPriorityResponse,
    mapProgresses,
    mapProjectResponse,
    mapProjectResponses,
    mapRoles,
    mapTeamResponse,
    mapUserResponse
} from '../mapper/mapper';
import {blobRequest, request} from '../../security/auth';
import ErrorResponse from '../errors/error-response';
import generateErrorToasts from '../../screens/notifications/toast-util';
import { HTTP_METHODS, URLS } from '../../config/path';

/**
 * Fetches all teams from the server.
 *
 * @returns A promise that resolves to an array of LongTeam objects or an empty array if an error occurs.
 */
export function fetchTeams(): Promise<LongTeam[]> {
    return request(HTTP_METHODS.GET, URLS.TEAMS_PATH)
        .then((response) => mapTeamResponse(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return [];
        });
}

/**
 * Fetches all users from the server.
 *
 * @returns A promise that resolves to an array of LongUser objects or an empty array if an error occurs.
 */
export function fetchUsers(): Promise<LongUser[]> {
    return request(HTTP_METHODS.GET, URLS.USERS_PATH)
        .then((response) => mapUserResponse(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return [];
        });
}

/**
 * Fetches all possible priorities from the server.
 * 
 * @returns A promise that resolves to an array of Priority objects or an empty array if an error occurs.
 */
export function fetchPriorities(): Promise<Priority[]> {
    return request(HTTP_METHODS.GET, URLS.PRIORITIES_PATH)
        .then((response) => mapPriorityResponse(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return [];
        });
}

/**
 * Fetches all projects from the server.
 * 
 * @returns A promise that resolves to an array of Project objects or an empty array if an error occurs.
 */
export function fetchProjects(): Promise<Project[]> {
    return request(HTTP_METHODS.GET, URLS.PROJECTS_PATH)
        .then((response) => mapProjectResponses(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return [];
        });
}

/**
 * Fetches a specific project by its ID.
 * 
 * @param id ID of the project to be fetched.
 * @returns A promise that resolves to a Project object or null if not found.
 */
export function fetchProject(id: number): Promise<Project | null> {
    return request(HTTP_METHODS.GET, `${URLS.PROJECTS_PATH}/${id}`)
        .then((response) => mapProjectResponse(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return null;
        });
}

/**
 * Fetches all roles assignable to users from the server.
 * 
 * @returns A promise that resolves to an array of role strings or an empty array if an error occurs.
 */
export function fetchRoles(): Promise<string[]> {
    return request(HTTP_METHODS.GET, `${URLS.USERS_PATH}/roles`)
        .then((response) => mapRoles(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return [];
        });
}

/**
 * Fetches all labels from the server.
 * 
 * @returns A promise that resolves to an array of Label objects or an empty array if an error occurs.
 */
export function fetchLabels(): Promise<Label[]> {
    return request(HTTP_METHODS.GET, URLS.LABELS_PATH)
        .then((response) => mapLabels(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return [];
        });
}

/**
 * Fetches the frame count for a specific project.
 * 
 * @param projectId ID of the project for which to fetch the frame count.
 * @returns A promise that resolves to the number of frames in the project or 0 if an error occurs.
 */
export function fetchFrameCount(projectId: number): Promise<number> {
    return request(HTTP_METHODS.GET, `${URLS.PROJECTS_PATH}/${projectId}/frame/count`)
        .then((response) => mapFrameCount(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return 0;
        });
}

/**
 * Fetches all annotations for a specific project.
 * 
 * @param projectId ID of the project for which to fetch annotations.
 * @returns A promise that resolves to an array of Annotation objects or an empty array if an error occurs.
 */
export function fetchAnnotations(projectId: number): Promise<Annotation[]> {
    return request(HTTP_METHODS.GET, `${URLS.PROJECTS_PATH}/${projectId}/annotations`)
        .then((response) => mapAnnotations(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return [];
        });
}

/**
 * Fetches all progresses for projects.
 * 
 * @returns A promise that resolves to an array of Progress objects or an empty array if an error occurs.
 */
export function fetchProgresses(): Promise<Progress[]> {
    return request(HTTP_METHODS.GET, `${URLS.PROJECTS_PATH}/progresses`)
        .then((response) => mapProgresses(response.data))
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
            return [];
        });
}

/**
 * Fetches the image URL for a specific frame in a project.
 * 
 * @param project The project from which to fetch the image.
 * @param position The position of the frame in the project (1-based index).
 * @returns A promise that resolves to the URL of the image blob or an empty string if an error occurs.
 */
export async function getImageUrlRequest(project: Project, position: number) {
  const response = await blobRequest(`/api/projects/${project.id}/frame/${position - 1}`);
  const blob = new Blob([response.data], { type: response.headers['content-type'] });
  const imageUrl = URL.createObjectURL(blob);
  return imageUrl;
}


/**
 * Fetch exported data for a specific project.
 * 
 * @param projectId ID of the project to export data from.
 * @returns A promise that resolves to the exported data or an error message.
 */
export async function getExportedData(projectId: number) {
    const response = await request(HTTP_METHODS.GET, `/api/projects/${projectId}/export`);
    return response.data;
}

