import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import {
    MDBContainer,
    MDBInput,
    MDBBtn,
} from 'mdb-react-ui-kit';

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const history = useNavigate();

    const handleLogin = async () => {
        try {
            if (!username || !password) {
                setError('Please enter both username and password.');
                return;
            }

            const response = await axios.post('/api/login', { username, password });
            console.log('Login successful:', response.data);
            history('/');
        } catch (error) {
            console.error('Login failed:');
            setError('Invalid username or password.');
        }
    };

    return (
        <div>
            <div>
                <MDBContainer>
                    <h2>Login</h2>
                    <MDBInput placeholder='Email address' id='email' value={username} type='email' onChange={(e) => setUsername(e.target.value)} />
                    <MDBInput placeholder='Password' id='password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} />
                    <button onClick={handleLogin}>Sign in</button>
                </MDBContainer>
            </div>
        </div>
    );
}

export default LoginPage;