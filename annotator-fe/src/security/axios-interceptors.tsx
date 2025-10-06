import axios, { AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { 
    getAuthToken, 
    getRefreshToken, 
    refreshTokenRequest, 
    setTokensFromResponse, 
    invalidateToken,
    refreshAccessTokenIfNeeded 
} from './auth';

let isRefreshing = false;
let failedQueue: Array<{
    resolve: (value: string | null) => void;
    reject: (error: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
    failedQueue.forEach(({ resolve, reject }) => {
        if (error) {
            reject(error);
        } else {
            resolve(token);
        }
    });
    
    failedQueue = [];
};

/**
 * Sets up axios interceptors for automatic token refresh.
 */
export const setupAxiosInterceptors = () => {
    // Request interceptor to add token and refresh if needed
    axios.interceptors.request.use(
        async (config: InternalAxiosRequestConfig) => {
            // Skip token refresh for auth endpoints
            if (config.url?.includes('/api/auth/')) {
                const token = getAuthToken();
                if (token && !config.url.includes('/refresh') && !config.url.includes('/login') && !config.url.includes('/signup')) {
                    config.headers.Authorization = `Bearer ${token}`;
                }
                return config;
            }

            // Try to refresh token if needed before making request
            await refreshAccessTokenIfNeeded();
            
            const token = getAuthToken();
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            
            return config;
        },
        (error) => {
            return Promise.reject(error);
        }
    );

    // Response interceptor to handle 401 errors
    axios.interceptors.response.use(
        (response: AxiosResponse) => {
            return response;
        },
        async (error) => {
            const originalRequest = error.config;

            // If error is 401 and haven't already tried to refresh
            if (error.response?.status === 401 && !originalRequest._retry) {
                // Skip refresh attempts for auth endpoints
                if (originalRequest.url?.includes('/api/auth/')) {
                    return Promise.reject(error);
                }

                // Check if this is a BAD_CREDENTIALS error
                const errorData = error.response?.data;
                const isAuthError = errorData?.errors?.[0]?.error === 'BAD_CREDENTIALS' || errorData?.error === 'TOKEN_INVALID';
                
                if (isAuthError) {
                    console.warn('JWT authentication failed - backend may have restarted. Logging out user.');
                    invalidateToken();
                    processQueue(error, null);
                    window.location.href = '/home';
                    return Promise.reject(error);
                }

                if (isRefreshing) {
                    // If already refreshing, queue this request
                    return new Promise((resolve, reject) => {
                        failedQueue.push({ resolve, reject });
                    }).then((token) => {
                        originalRequest.headers.Authorization = `Bearer ${token}`;
                        return axios(originalRequest);
                    }).catch((err) => {
                        return Promise.reject(err);
                    });
                }

                originalRequest._retry = true;
                isRefreshing = true;

                const refreshToken = getRefreshToken();
                if (!refreshToken) {
                    invalidateToken();
                    processQueue(error, null);
                    isRefreshing = false;
                    // Redirect to login page
                    window.location.href = '/login';
                    return Promise.reject(error);
                }

                try {
                    const response = await refreshTokenRequest(refreshToken);
                    const newAccessToken = response.data.accessToken;
                    
                    setTokensFromResponse(response.data);
                    processQueue(null, newAccessToken);
                    
                    // Retry the original request with new token
                    originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                    return axios(originalRequest);
                } catch (refreshError) {
                    invalidateToken();
                    processQueue(refreshError, null);
                    // Redirect to login page
                    window.location.href = '/login';
                    return Promise.reject(refreshError);
                } finally {
                    isRefreshing = false;
                }
            }

            return Promise.reject(error);
        }
    );
};

/**
 * Removes axios interceptors.
 */
export const removeAxiosInterceptors = () => {
    axios.interceptors.request.clear();
    axios.interceptors.response.clear();
};