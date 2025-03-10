import { invalidateToken } from './auth';

export const logout = async (navigate: () => void) => {
  invalidateToken();
  navigate();
};
