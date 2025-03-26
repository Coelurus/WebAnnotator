import toast from 'react-hot-toast';
import {request} from '../../security/auth';
import {ProjectRequest, TeamRequest} from '../model/requests';

export function updateProject(projectId: number, projectData: ProjectRequest): Promise<void> {
    const requestPromise = request('PUT', `/api/projects/${projectId}`, projectData)
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

export function updateTeam(teamId: number, teamData: TeamRequest): Promise<void> {
    const requestPromise = request('PUT', `/api/teams/${teamId}`, teamData)
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

export function updateUser(userId: number, userData: UserRequest): Promise<void> {
    const requestPromise = request('PUT', `/api/users/${userId}`, userData)
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