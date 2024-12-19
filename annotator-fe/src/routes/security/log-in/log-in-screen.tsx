import { useState } from 'react';
import { login } from './log-in-fun';


function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    return (
        <div>
            <div>
                <form>
                    <h2>Login</h2>
                    <input placeholder='Username' id='username' value={username} type='text' onChange={(e) => setUsername(e.target.value)} />
                    <input placeholder='Password' id='password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} />
                    <button onClick={() => login(username, password)}>Sign in</button>
                </form>
            </div>
        </div>
    );
}

export default LoginPage;