import { request, setAuthToken } from '../../../security/auth';

export const login = async (
    username: string, 
    password: string, 
    onSuccess: () => void, 
    onError: (error: string) => void
) => {
    if (!username || !password) {
        onError('Please enter both username and password.');
        return;
    }

    try {
        const response = await request('POST', '/api/auth/login', {
            username: username,
            password: password,
        });

        setAuthToken(response.data.token);
        onSuccess(); 
    } catch (error) {
        setAuthToken(null);
        onError('Invalid credentials');
    }
};