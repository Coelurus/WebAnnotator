const BASE_URL = '/api';

export const URLS = {
    GET_USERS: `${BASE_URL}/users`,
    GET_USER_DETAILS: (userId: string) => `${BASE_URL}/users/${userId}`,
    CREATE_USER: `${BASE_URL}/users/create`,
    UPDATE_USER: (userId: string) => `${BASE_URL}/users/update/${userId}`,
    DELETE_USER: (userId: string) => `${BASE_URL}/users/delete/${userId}`,
};