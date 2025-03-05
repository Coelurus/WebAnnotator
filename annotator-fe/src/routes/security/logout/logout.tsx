import { setAuthToken } from '../../../security/auth';

export const logout = async (navigate: () => void) => {
  setAuthToken(null);
  navigate();
};
