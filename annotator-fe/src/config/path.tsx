const BASE_URL = '/api';

/**
 * URLS object contains all the API endpoints for BE calls.
 * Each endpoint is defined as a string or a function that returns a string.
 */
export const URLS = {
    GET_USERS: `${BASE_URL}/users`,
    GET_USER_DETAILS: (userId: string) => `${BASE_URL}/users/${userId}`,
    CREATE_USER: `${BASE_URL}/users/create`,
    UPDATE_USER: (userId: string) => `${BASE_URL}/users/update/${userId}`,
    DELETE_USER: (userId: string) => `${BASE_URL}/users/delete/${userId}`,
};