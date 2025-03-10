import { ErrorResponse } from '../persistence/errors/error-response';
import { invalidateToken, loginRequest, setAuthToken } from './auth';

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
