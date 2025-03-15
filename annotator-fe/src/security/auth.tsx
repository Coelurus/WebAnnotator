import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import {
  LabelRequest,
  ProjectRequest,
  TeamRequest,
  UserRequest
} from '../persistence/model/requests';

export default interface JwtPayload {
  sub: string;
  role: string;
  exp: number;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface SignupCredentials {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
}

export const isUserAdmin = () => {
  const token = getAuthToken();
  if (isTokenValid(token)) {
    const decoded: JwtPayload = jwtDecode(token!);
    return decoded.role === 'ROLE_ADMIN';
  }
  return false;
};

export const isUserLoggedIn = () => {
  const token = getAuthToken();
  return isTokenValid(token);
};

const isTokenValid = (token: string | null) => {
  if (token !== null) {
    const decoded: JwtPayload = jwtDecode(token);
    return Date.now() <= decoded.exp * 1000;
  }
  return false;
};

export const getUserUsername = () => {
  const token = getAuthToken();
  if (isTokenValid(token)) {
    const decoded: JwtPayload = jwtDecode(token!);
    return decoded.sub;
  }
};

export const getAuthToken = () => {
  return window.localStorage.getItem('auth_token');
};

export const invalidateToken = () => {
  setAuthToken(null);
};

export const setAuthToken = (token: string | null) => {
  if (token !== null) {
    window.localStorage.setItem('auth_token', token);
  } else {
    window.localStorage.removeItem('auth_token');
  }
};

export const request = (
  method: string,
  url: string,
  data:
    | Record<string, string>
    | UserRequest
    | FormData
    | LabelRequest
    | TeamRequest
    | ProjectRequest = {},
  contentType: string = '',
  responseType: string = ''
) => {
  return axios({
    method: method,
    url: url,
    headers: {
      ...(getAuthToken() !== null && getAuthToken() !== 'null'
        ? { Authorization: `Bearer ${getAuthToken()}` }
        : {}),
      ...(contentType ? { 'Content-Type': contentType } : {}),
      ...(responseType ? { 'Response-Type': responseType } : {})
    },
    data: data
  });
};

export const loginRequest = (data: LoginCredentials) => {
  return axios({
    method: 'POST',
    url: '/api/auth/login',
    data: data
  });
};

export const signupRequest = (data: SignupCredentials) => {
  return axios({
    method: 'POST',
    url: '/api/auth/signup',
    data: data
  });
};

export const blobRequest = (url: string) => {
  return axios.get(url, {
    responseType: 'blob',
    headers: {
      ...(getAuthToken() !== null && getAuthToken() !== 'null'
        ? { Authorization: `Bearer ${getAuthToken()}` }
        : {})
    }
  });
};
