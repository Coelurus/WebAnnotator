import axios from 'axios';

export const logout = async () => {
    try {
        await axios.post('/api/logout');
        alert('Logout successful!');
    } catch (error) {
        alert('Logout failed');
    }
};