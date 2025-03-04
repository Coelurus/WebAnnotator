import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from './login';
import { Button, Card, Form } from 'react-bootstrap';

interface LoginValidationErrors {
    username?: String,
    password?: String,
}

function LoginForm() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [validationErrors, setValidationErrors] = useState<LoginValidationErrors>({});
    const navigate = useNavigate();

    const handleLogin = async () => {
        let errors: LoginValidationErrors = {};

        if (!username.trim()) errors.username = "Username cannot be empty";
        if (!password.trim()) errors.password = "Password cannot be empty";

        setValidationErrors(errors);
        if (Object.keys(errors).length > 0) return;

        await login(
            username,
            password,
            () => navigate(0),
            (errorMessage) => setError(errorMessage)
        );
    };

    return (
        <Card className="mt-4 p-4 shadow-sm" style={{ maxWidth: "400px", width: "100%" }}>
          <h3 className="text-center">Login</h3>
          <Form onSubmit={(e) => e.preventDefault()}>
            <Form.Group className="mb-3">
              <Form.Control
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                isInvalid={!!validationErrors.username}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Control
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                isInvalid={!!validationErrors.password}
              />
            </Form.Group>
            {error && <p className="text-danger text-center">{error}</p>}
            <Button variant="primary" className="w-100" onClick={handleLogin}>
              Sign in
            </Button>
          </Form>
        </Card>
      );
}

export default LoginForm;