import toast from 'react-hot-toast';
import {request} from '../../security/auth';
import ErrorResponse from '../errors/error-response';
import {LabelApiResponse, LongTeamApiResponse, ProjectApiResponse, LongUserApiResponse} from '../model/api-responses';
import {LabelRequest, TeamRequest, UserRequest} from '../model/requests';
import {ShortTeam} from '../model/data';
import generateErrorToasts from '../../screens/notifications/toast-util';
import { HTTP_METHODS, URLS } from '../../config/path';

/**
 * Makes a POST request to erase annotations in a project within a specified index range.
 * Shows error toasts if the request fails.
 * 
 * @param projectId ID of the project where annotations will be erased.
 * @param lowerIndex Lower index of the range of annotations to be erased.
 * @param upperIndex Upper index of the range of annotations to be erased.
 */
export function postEraseAnnotations(projectId: number, lowerIndex: number, upperIndex: number) {
    request(HTTP_METHODS.POST, `${URLS.PROJECTS_PATH}/${projectId}/erase/${lowerIndex}/${upperIndex}`).catch(
        (error: ErrorResponse) => {
            generateErrorToasts(error);
        }
    );
}

/**
 * Makes a POST request to add annotations in a project within a specified index range and label.
 * Shows error toasts if the request fails.
 * 
 * @param projectId ID of the project where annotations will be added.
 * @param lowerIndex Lower index of the range of annotations to be added.
 * @param upperIndex Upper index of the range of annotations to be added.
 * @param labelId ID of the label to be applied to the annotations.
 */
export function postAddAnnotations(
    projectId: number,
    lowerIndex: number,
    upperIndex: number,
    labelId: number
) {
    request(
        HTTP_METHODS.POST,
        `${URLS.PROJECTS_PATH}/${projectId}/annotate/${lowerIndex}/${upperIndex}/label/${labelId}`
    ).catch((error: ErrorResponse) => {
        generateErrorToasts(error);
    });
}

/**
 * Makes a POST request to create a new label.
 * Shows success or error toasts based on the outcome of the request.
 * 
 * @param newLabel The label to be created.
 * @returns A promise that resolves to the created label or null if an error occurs.
 */
export function postCreateLabel(newLabel: LabelRequest): Promise<LabelApiResponse> {
    const requestPromise = request(HTTP_METHODS.POST, URLS.LABELS_PATH, newLabel)
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

/**
 * Makes a POST request to create a new project.
 * Shows success or error toasts based on the outcome of the request.
 * 
 * @param formData The form data containing project details.
 * @returns A promise that resolves to the created project or null if an error occurs.
 */
export function createProjectRequest(formData: FormData): Promise<ProjectApiResponse<ShortTeam>> {
    const requestPromise = request(HTTP_METHODS.POST, URLS.PROJECTS_PATH, formData)
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

/**
 * Makes a POST request to create a new user.
 * Shows success or error toasts based on the outcome of the request.
 * 
 * @param newUser Request object containing details of user to be created.
 * @returns A promise that resolves to the created user or null if an error occurs.
 */
export function createUserRequest(newUser: UserRequest): Promise<LongUserApiResponse> {
    const requestPromise = request(HTTP_METHODS.POST, URLS.USERS_PATH, newUser)
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

/**
 * Makes a POST request to create a new team.
 * Shows success or error toasts based on the outcome of the request.
 * 
 * @param newTeam Request object containing details of team to be created.
 * @returns 
 */
export function createTeamRequest(newTeam: TeamRequest): Promise<LongTeamApiResponse> {
    const requestPromise = request(HTTP_METHODS.POST, URLS.TEAMS_PATH, newTeam)
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

/**
 * Makes a POST request to add a user to a team.
 * Shows success or error toasts based on the outcome of the request.
 * 
 * @param userId ID of the user to be added to the team.
 * @param teamId ID of the team to which the user will be added.
 * @returns A promise that resolves when the user is successfully added to the team.
 */
export function addTeamMember(userId: number, teamId: number): Promise<void> {
    const requestPromise = request(HTTP_METHODS.POST, `${URLS.TEAMS_PATH}/${teamId}/members/${userId}`)
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
/**
 * Send annotated data to the AI model for processing.
 *
 * @param projectId ID of the project on which to train the AI model.
 * @returns A promise that resolves to the AIModelUpdateResponse from the server.
 */

export async function getAIAnnotatedResult(projectId: number) {
    const response = await request(HTTP_METHODS.POST, `/api/projects/${projectId}/trainAI`);
    return response.data;
}

/**
 * Makes a POST request to predict gestures for a project using AI.
 * Shows error toasts if the request fails.
 * 
 * @param projectId ID of the project to predict gestures for.
 * @returns A promise that resolves to the PredictionResponse from the server.
 */
export async function getAIPrediction(projectId: number) {
    const response = await request(HTTP_METHODS.POST, `/api/projects/${projectId}/predict`);
    return response.data;
}
