import generateErrorToasts from '../screens/notifications/toast-util';
import { invalidateToken, setTokensFromResponse, signupRequest } from './auth';

/**
 * Function to handle user signup.
 * 
 * @param username The username of the user attempting to sign up.
 * @param password The password of the user attempting to sign up.
 * @param firstName The first name of the user attempting to sign up.
 * @param lastName The last name of the user attempting to sign up.
 * @param onSuccess Callback function to be called on successful signup.
 * @param onError Callback function to be called on signup error.
 */
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
      setTokensFromResponse(response.data);
      onSuccess();
    })
    .catch((error) => {
      generateErrorToasts(error);
      invalidateToken();
      onError('Signup failed');
    });
};
