import toast from 'react-hot-toast';
import {request} from '../../security/auth';
import {ErrorResponse} from '../errors/error-response';
import {LabelApiResponse, LongTeamApiResponse, ProjectApiResponse, UserApiResponse} from '../model/api-responses';
import {LabelRequest, TeamRequest, UserRequest} from '../model/requests';
import {ShortTeam} from '../model/data';
import generateErrorToasts from '../../screens/notifications/toast-util';

export function postEraseAnnotations(projectId: number, lowerIndex: number, higherIndex: number) {
    request('POST', `./api/projects/${projectId}/erase/${lowerIndex}/${higherIndex}`).catch(
        (error: ErrorResponse) => {
            generateErrorToasts(error);
        }
    );
}

export function postAddAnnotations(
    projectId: number,
    lowerIndex: number,
    higherIndex: number,
    labelId: number
) {
    request(
        'POST',
        `./api/projects/${projectId}/annotate/${lowerIndex}/${higherIndex}/label/${labelId}`
    ).catch((error: ErrorResponse) => {
        generateErrorToasts(error);
    });
}

export function postCreateLabel(newLabel: LabelRequest): Promise<LabelApiResponse> {
    const requestPromise = request('POST', './api/labels', newLabel)
        .then((response) => response.data)
        .catch(() => {
            throw new Error(`Label with name ${newLabel.labelName} already exists.`);
        });

    return toast
        .promise(requestPromise, {
            loading: 'Creating label...',
            success: 'Label created successfully!',
            error: `Label with name ${newLabel.labelName} already exists.`
        })
        .catch(() => null);
}

export function createProjectRequest(formData: FormData): Promise<ProjectApiResponse<ShortTeam>> {
    const requestPromise = request('POST', './api/projects', formData)
        .then((response) => response.data)
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
        });

    return toast.promise(requestPromise, {
        loading: 'Creating project...',
        success: <b>Project created successfully!</b>,
        error: <b>Could not create project.</b>
    });
}

export function createUserRequest(newUser: UserRequest): Promise<UserApiResponse> {
    const requestPromise = request('POST', './api/users', newUser)
        .then((response) => response.data)
        .catch((error: ErrorResponse) => {
            generateErrorToasts(error);
        });

    return toast.promise(requestPromise, {
        loading: 'Adding user...',
        success: 'User added successfully!',
        error: 'Error adding user.'
    });
}

export function createTeamRequest(newTeam: TeamRequest): Promise<LongTeamApiResponse> {
    const requestPromise = request('POST', './api/teams', newTeam)
        .then((response) => response.data)
        .catch(() => {
            throw new Error(`Adding team failed.`);
        });

    return toast.promise(requestPromise, {
        loading: 'Adding team...',
        success: 'Team added successfully!',
        error: 'Error adding team.'
    });
}

export function addTeamMember(userId: number, teamId: number): Promise<void> {
    const requestPromise = request('POST', `./api/teams/${teamId}/members/${userId}`)
        .then(() => {
        })
        .catch(() => {
            throw new Error('Add member to team failed');
        });

    return toast.promise(requestPromise, {
        loading: 'Adding member to team...',
        success: 'Member added successfully!',
        error: 'Error adding member to team'
    });
}
