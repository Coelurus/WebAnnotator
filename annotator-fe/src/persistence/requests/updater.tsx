import toast from 'react-hot-toast';
import {request} from '../../security/auth';
import {ProjectRequest, TeamRequest, UserRequest} from '../model/requests';
import { HTTP_METHODS, URLS } from '../../config/path';

/**
 * Makes a PUT request to update a project.
 * Shows success or error toasts based on the outcome of the request.
 * 
 * @param projectId ID of the project to be updated.
 * @param projectData Request object containing updated project details.
 * @returns A promise that resolves when the project is successfully updated.
 */
export function updateProject(projectId: number, projectData: ProjectRequest): Promise<void> {
    const requestPromise = request(HTTP_METHODS.PUT, `${URLS.PROJECTS_PATH}/${projectId}`, projectData)
        .then(() => {
        })
        .catch(() => {
            throw new Error('Update project failed');
        });
    return toast.promise(requestPromise, {
        loading: 'Updating project...',
        success: <b>Project updated successfully!</b>,
        error: <b>Could not update project.</b>
    });
}

/**
 * Makes a PUT request to update a team.
 * Shows success or error toasts based on the outcome of the request.
 * 
 * @param teamId ID of the team to be updated.
 * @param teamData Request object containing updated team details.
 * @returns A promise that resolves when the team is successfully updated.
 */
export function updateTeam(teamId: number, teamData: TeamRequest): Promise<void> {
    const requestPromise = request(HTTP_METHODS.PUT, `${URLS.TEAMS_PATH}/${teamId}`, teamData)
        .then(() => {
        })
        .catch(() => {
            throw new Error('Update team failed');
        });
    return toast.promise(requestPromise, {
        loading: 'Updating team...',
        success: <b>Team updated successfully!</b>,
        error: <b>Could not update team.</b>
    });
}

/**
 * Makes a PUT request to update a user.
 * Shows success or error toasts based on the outcome of the request.
 * 
 * @param userId ID of the user to be updated.
 * @param userData Request object containing updated user details.
 * @returns A promise that resolves when the user is successfully updated.
 */
export function updateUser(userId: number, userData: UserRequest): Promise<void> {
    const requestPromise = request(HTTP_METHODS.PUT, `${URLS.USERS_PATH}/${userId}`, userData)
        .then(() => {
        })
        .catch(() => {
            throw new Error('Update user failed');
        });
    return toast.promise(requestPromise, {
        loading: 'Updating user...',
        success: <b>User updated successfully!</b>,
        error: <b>Could not update user.</b>
    });
}