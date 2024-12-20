import React from 'react';
import { logout } from './log-out-fun';

const LogoutButton = () => {
    const handleLogout = async () => {
        await logout();
        //window.location.href = '/login'; 
    };

    return (
        <button onClick={handleLogout}>Logout</button>
    );
};

export default LogoutButton;
