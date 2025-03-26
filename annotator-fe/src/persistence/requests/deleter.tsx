import {request} from '../../security/auth';
import {ErrorResponse} from '../errors/error-response';
import generateErrorToasts from '../../screens/notifications/toast-util';

export function deleteTeamRequest(teamId: number): Promise<void> {
    return request('DELETE', `/api/teams/${teamId}`)
        .then(() => {
        })
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
        });
}

export function deleteProjectRequest(projectId: number): Promise<void> {
    return request('DELETE', `/api/projects/${projectId}`)
        .then(() => {
        })
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
        });
}
