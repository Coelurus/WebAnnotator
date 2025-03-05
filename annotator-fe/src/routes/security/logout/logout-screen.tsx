import { useNavigate } from 'react-router-dom';
import { logout } from './logout';
import React from 'react';

function LogoutButton() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout(() => navigate('/'));
  };

  return (
    <ul>
      <button type="button" onClick={handleLogout}>
        Sign out
      </button>
    </ul>
  );
}

export default LogoutButton;
