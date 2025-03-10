import { invalidateToken, setAuthToken, signupRequest } from './auth';

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

  await signupRequest({
    username: username,
    password: password,
    firstName: firstName,
    lastName: lastName
  })
    .then((response) => {
      setAuthToken(response.data.token);
      onSuccess();
    })
    .catch((error) => {
      console.error(error);
      invalidateToken();
      onError('Signup failed');
    });
};
