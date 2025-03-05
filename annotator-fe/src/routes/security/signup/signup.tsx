import { request, setAuthToken } from '../../../security/auth';

export const signup = async (
  username: string,
  password: string,
  firstName: string,
  lastName: string,
  onSuccess: () => void,
  onError: (error: string) => void
) => {
  if (!username || !password || !firstName || !lastName) {
    onError('Please fill in all fields');
    return;
  }

  try {
    const response = await request('POST', '/api/auth/signup', {
      username: username,
      password: password,
      firstName: firstName,
      lastName: lastName
    });

    setAuthToken(response.data.token);
    onSuccess();
  } catch (error) {
    setAuthToken(null);
    onError('Invalid stuff...');
  }
};
