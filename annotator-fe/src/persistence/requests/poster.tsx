import React from 'react';
import toast from 'react-hot-toast';
import { request } from '../../security/auth';
import { ErrorResponse } from '../errors/error-response';
import { LabelApiResponse, ProjectApiResponse, UserApiResponse } from '../model/api-responses';
import { LabelRequest, UserRequest } from '../model/requests';
import { ShortTeam } from '../model/data';
import generateErrorToasts from '../../screens/notifications/toast-util';

export function postEraseAnnotations(projectId: number, lowerIndex: number, higherIndex: number) {
  request('POST', `/api/projects/${projectId}/erase/${lowerIndex}/${higherIndex}`).catch(
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
    `/api/projects/${projectId}/annotate/${lowerIndex}/${higherIndex}/label/${labelId}`
  ).catch((error: ErrorResponse) => {
    generateErrorToasts(error);
  });
}

export function postCreateLabel(newLabel: LabelRequest): Promise<LabelApiResponse> {
  const requestPromise = request('POST', '/api/labels', newLabel)
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
  const requestPromise = request('POST', '/api/projects', formData)
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
  const requestPromise = request('POST', '/api/users', newUser)
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
