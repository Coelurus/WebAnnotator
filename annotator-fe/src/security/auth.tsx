import axios from "axios";

export const getAuthToken = () => {
    return window.localStorage.getItem('auth_token');
}

export const setAuthToken = (token: string|null) => {
    if (token !== null) {
        window.localStorage.setItem('auth_token', token);
    } else {
        window.localStorage.removeItem('auth_token')
    }
}

export const request = (method: string, url: string, data: Record<string, string>) => {
    return axios(
        {
            method: method,
            url: url,
            headers: (getAuthToken() !== null && getAuthToken() !== "null") 
                ? {'Authorization': `Bearer ${getAuthToken()}`} 
                : {},
            data: data
        }
    )
}