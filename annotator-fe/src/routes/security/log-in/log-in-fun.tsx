import axios from 'axios';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';


export const login = async (username: string, password: string) => {
    const [error, setError] = useState<string>('');
    const history = useNavigate();

    try {
        if (!username || !password) {
            setError('Please enter both username and password.');
            return;
        }

        const response = await axios.post('/login', { username, password });
        console.log('Login successful:', response.data);
        history('/');
    } catch (error) {
        console.error('Login failed:');
        setError('Invalid username or password.');
    }
};