import axios from 'axios';
import {jwtDecode} from 'jwt-decode';
import {LabelRequest, ProjectRequest, TeamRequest, UserRequest} from '../persistence/model/requests';
import { HTTP_METHODS, URLS } from '../config/path';

/**
 * Interface representing the structure of the JWT payload.
 */
export default interface JwtPayload {
    /**
     * The subject of the token.
     */
    sub: string;
    /**
     * The role of the user
     */
    role: string;
    /**
     * The expiration time of the token in seconds since the epoch.
     */
    exp: number;
}

/**
 * Interface for the login credentials used during authentication.
 */
export interface LoginCredentials {
    /**
     * The username of the user attempting to log in.
     */
    username: string;
    /**
     * The password of the user attempting to log in.
     */
    password: string;
}

/**
 * Interface for the signup credentials used during user registration.
 */
export interface SignupCredentials {
    /**
     * The username of the user registering.
     */
    username: string;
    /**
     * The password of the user registering.
     */
    password: string;
    /**
     * The first name of the user registering.
     */
    firstName: string;
    /**
     * The last name of the user registering.
     */
    lastName: string;
}

/**
 * Function to check if the logged-in user is an admin.
 * 
 * @returns True if the user is an admin, false otherwise.
 */
export const isUserAdmin = () => {
    const token = getAuthToken();
    if (isTokenValid(token)) {
        const decoded: JwtPayload = jwtDecode(token!);
        return decoded.role === 'ROLE_ADMIN';
    }
    return false;
};

/**
 * Function to check if the user is logged in by validating the JWT token.
 * 
 * @returns True if the user is logged in, false otherwise.
 */
export const isUserLoggedIn = () => {
    const token = getAuthToken();
    return isTokenValid(token);
};

/**
 * Function to validate the JWT token.
 * 
 * @param token The JWT token to validate.
 * @returns True if the token is valid, false otherwise.
 */
const isTokenValid = (token: string | null) => {
    if (token !== null) {
        const decoded: JwtPayload = jwtDecode(token);
        return Date.now() <= decoded.exp * 1000;
    }
    return false;
};

/**
 * Function to get the username of the logged-in user from the JWT token.
 * 
 * @returns The username of the user if the token is valid, undefined otherwise.
 */
export const getUserUsername = () => {
    const token = getAuthToken();
    if (isTokenValid(token)) {
        const decoded: JwtPayload = jwtDecode(token!);
        return decoded.sub;
    }
};

/**
 * Function to load the JWT token from local storage.
 * 
 * @return The JWT token if it exists, null otherwise.
 */
export const getAuthToken = () => {
    return window.localStorage.getItem('auth_token');
};

/**
 * Function to invalidate the JWT token by removing it from local storage.
 */
export const invalidateToken = () => {
    setAuthToken(null);
};

/**
 * Function to set the JWT token in local storage.
 * 
 * @param token The JWT token to set. If null, it removes the token from local storage.
 */
export const setAuthToken = (token: string | null) => {
    if (token !== null) {
        window.localStorage.setItem('auth_token', token);
    } else {
        window.localStorage.removeItem('auth_token');
    }
};

/**
 * Function to make an HTTP request using axios.
 * 
 * @param method Method of the request (GET, POST, PUT, DELETE, etc.)
 * @param url URL to which the request is made
 * @param data Data to be sent with the request, can be an object or FormData
 * @param contentType Content-Type of the request, e.g., 'application/json' or 'multipart/form-data'
 * @param responseType Response type expected from the server, e.g., 'json' or 'blob'
 * @returns A promise that resolves to the response of the request.
 */
export const request = (
    method: string,
    url: string,
    data:
        | UserRequest
        | LabelRequest
        | TeamRequest
        | FormData
        | Record<string, string>
        | ProjectRequest = {},
    contentType: string = '',
    responseType: string = ''
) => {
    return axios({
        method: method,
        url: url,
        headers: {
            ...(getAuthToken() !== null && getAuthToken() !== 'null'
                ? {Authorization: `Bearer ${getAuthToken()}`}
                : {}),
            ...(contentType ? {'Content-Type': contentType} : {}),
            ...(responseType ? {'Response-Type': responseType} : {})
        },
        data: data
    });
};

/**
 * Function to make a login request to the server.
 * 
 * @param data The login credentials containing username and password.
 * @returns A promise that resolves to the response of the login request.
 */
export const loginRequest = (data: LoginCredentials) => {
    return axios({
        method: HTTP_METHODS.POST,
        url: `${URLS.AUTH_PATH}/login`,
        data: data
    });
};

/**
 * Function to make a signup request to the server.
 * 
 * @param data The signup credentials containing username, password, first name, and last name.
 * @returns A promise that resolves to the response of the signup request.
 */
export const signupRequest = (data: SignupCredentials) => {
    return axios({
        method: HTTP_METHODS.POST,
        url: `${URLS.AUTH_PATH}/signup`,
        data: data
    });
};

/**
 * Function to make a request to download a file as a blob.
 * 
 * @param url The URL from which the file is to be downloaded.
 * @returns A promise that resolves to the response containing the file as a blob.
 */
export const blobRequest = (url: string) => {
    return axios.get(url, {
        responseType: 'blob',
        headers: {
            ...(getAuthToken() !== null && getAuthToken() !== 'null'
                ? {Authorization: `Bearer ${getAuthToken()}`}
                : {})
        }
    });
};
