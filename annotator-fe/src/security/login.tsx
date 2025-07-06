import ErrorResponse from '../persistence/errors/error-response';
import { invalidateToken, loginRequest, setAuthToken } from './auth';

/**
 * Function to handle user login.
 *
 * @param username The username of the user attempting to log in.
 * @param password The password of the user attempting to log in.
 * @param onSuccess Callback function to be called on successful login.
 * @param onError Callback function to be called on login error.
 */
export const login = async (
  username: string,
  password: string,
  onSuccess: () => void,
  onError: (error: string) => void
) => {
  if (!username || !password) {
    onError('Please enter both username and password.');
    return;
  }

  await loginRequest({
    username: username,
    password: password
  })
    .then((response) => {
      setAuthToken(response.data.token);
      onSuccess();
    })
    .catch((error: ErrorResponse) => {
      invalidateToken();
      onError(error.response.data.errors[0]?.message);
    });
};
