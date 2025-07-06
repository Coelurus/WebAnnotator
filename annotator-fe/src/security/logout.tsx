import { invalidateToken } from './auth';

/**
 * Function to handle user logout.
 * This function invalidates the user's token and navigates to the login page.
 *
 * @param navigate Function to navigate to the login page after logout.
 */
export const logout = async (navigate: () => void) => {
  invalidateToken();
  navigate();
};
