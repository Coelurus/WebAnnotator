import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // Import useHistory hook
import {
    MDBContainer,
    MDBInput,
    MDBBtn,
} from 'mdb-react-ui-kit';

function SignupPage() {
    const [fullName, setFullName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('ROLE_CUSTOMER');
    const [error, setError] = useState('');
    const history = useNavigate(); 

    const handleSignup = async () => {
        try {
            if (!fullName || !email || !password ) {
                setError('Please fill in all fields.');
                return;
            }

            const response = await axios.post('/api/signup', {
                fullName,
                email,
                password,
                role
            });
            console.log(response.data);
            history('/');
        } catch (error) {
            console.error('Signup failed:');
        }
    };

    return (
        <div>
            <div>
                <MDBContainer>
                    <h2>Sign Up Page</h2>
                    {}
                    {error && <p>{error}</p>}
                    <MDBInput id='fullName' placeholder={"Full Name"} value={fullName} type='text'
                              onChange={(e) => setFullName(e.target.value)}/>
                    <MDBInput placeholder='Email Address' id='email' value={email} type='email'
                              onChange={(e) => setEmail(e.target.value)}/>
                    <MDBInput placeholder='Password' id='password' type='password' value={password}
                              onChange={(e) => setPassword(e.target.value)}/>

                    <label>Role:</label>
                    <select value={role} onChange={(e) => setRole(e.target.value)}>
                        <option value="ROLE_CUSTOMER">User</option>
                        <option value="ROLE_ADMIN">Admin</option>
                    </select>
                    <button onClick={handleSignup}>Sign Up
                    </button>
                </MDBContainer>
            </div>
        </div>
    );
}

export default SignupPage;