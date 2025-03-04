import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from './login';


function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        await login(
            username,
            password,
            () => navigate(0),
            (errorMessage) => setError(errorMessage)
        );
    };

    return (
        <div>
            <div>
                <form onSubmit={(e) => e.preventDefault()}>
                    <h2>Login</h2>
                    <input
                        placeholder='Username'
                        value={username}
                        type='text'
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <input
                        placeholder='Password'
                        type='password'
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    {error && <p style={{ color: 'red' }}>{error}</p>}
                    <button type="button" onClick={handleLogin}>Sign in</button>
                </form>
            </div>
        </div>
    );
}

export default LoginPage;