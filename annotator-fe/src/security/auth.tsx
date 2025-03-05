import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default interface JwtPayload {
  role: string;
}

export const isUserAdmin = () => {
  const token = getAuthToken();
  if (token !== null) {
    const decoded: JwtPayload = jwtDecode(token);
    return decoded.role === 'ROLE_ADMIN';
  }
  return false;
};

export const isUserLoggedIn = () => {
  return getAuthToken() !== null;
};

export const getAuthToken = () => {
  return window.localStorage.getItem('auth_token');
};

export const setAuthToken = (token: string | null) => {
  if (token !== null) {
    window.localStorage.setItem('auth_token', token);
  } else {
    window.localStorage.removeItem('auth_token');
  }
};

export const request = (method: string, url: string, data: Record<string, string> = {}) => {
  return axios({
    method: method,
    url: url,
    headers:
      getAuthToken() !== null && getAuthToken() !== 'null'
        ? { Authorization: `Bearer ${getAuthToken()}` }
        : {},
    data: data
  });
};
