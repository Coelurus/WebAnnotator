import React from 'react';
import { logout } from './log-out-fun'; // Import your logout API call

const LogoutButton = () => {
    const handleLogout = async () => {
        await logout();
        // Optionally redirect to the login page or clear local state
        window.location.href = '/login'; 
    };

    return (
        <button onClick={handleLogout}>Logout</button>
    );
};

export default LogoutButton;
