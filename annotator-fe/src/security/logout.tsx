import { invalidateToken, getRefreshToken, logoutRequest } from './auth';

/**
 * Function to handle user logout.
 * This function revokes the refresh token on the server and invalidates local tokens.
 *
 * @param navigate Function to navigate to the login page after logout.
 */
export const logout = async (navigate: () => void) => {
  const refreshToken = getRefreshToken();
  
  // If we have a refresh token, try to revoke it on the server
  if (refreshToken) {
    try {
      await logoutRequest(refreshToken);
    } catch {
      // Even if logout request fails, we still want to clear local tokens
      console.warn('Failed to revoke refresh token on server, clearing local tokens anyway');
    }
  }
  
  // Clear local tokens
  invalidateToken();
  navigate();
};
