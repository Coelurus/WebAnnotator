const BASE_URL = '/api';

/**
 * URLS object contains all the API endpoints for BE calls.
 * Each endpoint is defined as a string or a function that returns a string.
 */
export const URLS = {
   // GET_USERS: `${BASE_URL}/users`,
   // GET_USER_DETAILS: (userId: string) => `${BASE_URL}/users/${userId}`,
   // CREATE_USER: `${BASE_URL}/users/create`,
   // UPDATE_USER: (userId: string) => `${BASE_URL}/users/update/${userId}`,
   // DELETE_USER: (userId: string) => `${BASE_URL}/users/delete/${userId}`,

    /** 
     * URL path for the operations related to teams
     */
    TEAMS_PATH: `${BASE_URL}/teams`,
    /**
     * URL path for the operations related to users
     */
    USERS_PATH: `${BASE_URL}/users`,
    /**
     * URL path for the operations related to projects
     */
    PROJECTS_PATH: `${BASE_URL}/projects`,
    /**
     * URL path for the operations related to priorities
     */
    PRIORITIES_PATH: `${BASE_URL}/priorities`,
    /**
     * URL path for the operations related to labels
     */
    LABELS_PATH: `${BASE_URL}/labels`,
    /**
     * URL path for the operations related to authentication
     */
    AUTH_PATH: `${BASE_URL}/auth`,
};

/**
 * HTTP_METGHODS object contains all the HTTP methods used in API calls.
 * Each method is defined as a string.
 */
export const HTTP_METHODS = {
    /**
     * GET method for retrieving data from the server.
     */
    GET: 'GET',
    /**
     * POST method for sending data to the server.
     */
    POST: 'POST',
    /**
     * PUT method for updating data on the server.
     */
    PUT: 'PUT',
    /**
     * DELETE method for removing data from the server.
     */
    DELETE: 'DELETE',
};

/**
 * BUTTONS object contains mouse button identifiers.
 */
export const BUTTONS = {
    /**
     * Constants for left mouse button identifier.
     */
    LEFT_BUTTON: 0,
    /**
     * Constants for right mouse button identifier.
     */
    RIGHT_BUTTON: 2,
}