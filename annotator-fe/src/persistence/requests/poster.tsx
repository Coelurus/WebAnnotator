import { request } from '../../security/auth';
import { ErrorResponse } from '../errors/error-response';
import { LabelApiResponse } from '../model/api-responses';
import { LabelRequest, UserRequest } from '../model/requests';

export function postEraseAnnotations(
  projectId: number,
  lowerIndex: number,
  higherIndex: number,
  onError: (message: string) => void
) {
  request('POST', `/api/projects/${projectId}/erase/${lowerIndex}/${higherIndex}`).catch(
    (error) => {
      console.error(error);
      onError('Error erasing annotations');
    }
  );
}

export function postAddAnnotations(
  projectId: number,
  lowerIndex: number,
  higherIndex: number,
  labelId: number,
  onError: (message: string) => void
) {
  request(
    'POST',
    `/api/projects/${projectId}/annotate/${lowerIndex}/${higherIndex}/label/${labelId}`
  ).catch((error: ErrorResponse) => {
    console.error(error.response);
    onError('Error adding annotations');
  });
}

export function postCreateLabel(
  newLabel: LabelRequest,
  onError: (message: string) => void
): Promise<LabelApiResponse> {
  return request('POST', '/api/labels', newLabel)
    .then((response) => response.data)
    .catch((error) => {
      console.error(error);
      onError(`Label with name ${newLabel.labelName} already exists.`);
      return null;
    });
}

export function createProjectRequest(formData: FormData, onError: (message: string) => void) {
  request('POST', '/api/projects', formData).catch((error) => {
    console.error(error);
    onError(`Failed to create project}`);
  });
}

export function createUserRequest(
  newUser: UserRequest,
  onError: (message: string) => void
): Promise<void> {
  return request('POST', '/api/users', newUser)
    .then(() => {})
    .catch((error) => {
      console.error(error);
      onError('Error adding user');
    });
}
