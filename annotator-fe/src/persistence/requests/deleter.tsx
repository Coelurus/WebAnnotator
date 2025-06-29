import {request} from '../../security/auth';
import ErrorResponse from '../errors/error-response';
import generateErrorToasts from '../../screens/notifications/toast-util';
import { HTTP_METHODS, URLS } from '../../config/path';

/**
 * Deletes a team by its ID.
 * 
 * @param teamId ID of the team to be deleted.
 * @returns A promise that resolves when the team is successfully deleted.
 */
export function deleteTeamRequest(teamId: number): Promise<void> {
    return request(HTTP_METHODS.DELETE, `${URLS.TEAMS_PATH}/${teamId}`)
        .then(() => {
        })
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
        });
}

/**
 * Deletes a project by its ID.
 * 
 * @param projectId ID of the project to be deleted.
 * @returns A promise that resolves when the project is successfully deleted.
 */
export function deleteProjectRequest(projectId: number): Promise<void> {
    return request(HTTP_METHODS.DELETE, `${URLS.PROJECTS_PATH}/${projectId}`)
        .then(() => {
        })
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
        });
}

/**
 * Deletes a user by its ID.
 * 
 * @param userId ID of the user to be deleted.
 * @returns A promise that resolves when the user is successfully deleted.
 */
export function deleteUserRequest(userId: number): Promise<void> {
    return request(HTTP_METHODS.DELETE, `${URLS.USERS_PATH}/${userId}`)
        .then(() => {
        })
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
        });
}