import { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Import useHistory hook
import { signup } from './signup';


function SignupPage() {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate(); 

    const handleSignup = async () => {
        await signup(
            username,
            password,
            firstName,
            lastName,
            () => navigate('/'),
            (errorMessage) => setError(errorMessage)
        );
    };

    return (
        <div>
            <div>
                <form onSubmit={(e) => e.preventDefault()}>
                    <h2>Sign Up Page</h2>
                    <input id='username' placeholder='Username' value={username} type='text'
                              onChange={(e) => setUsername(e.target.value)}/>
                    <input id='password' placeholder='Password' type='password' value={password}
                              onChange={(e) => setPassword(e.target.value)}/>
                    <input id='firstName' placeholder={"First Name"} value={firstName} type='text'
                              onChange={(e) => setFirstName(e.target.value)}/>
                    <input id='lastName' placeholder={"Last Name"} value={lastName} type='text'
                              onChange={(e) => setLastName(e.target.value)}/>
                    {error && <p style={{ color: 'red' }}>{error}</p>}
                    <button onClick={handleSignup}>Sign Up</button>
                </form>
            </div>
        </div>
    );
}

export default SignupPage;