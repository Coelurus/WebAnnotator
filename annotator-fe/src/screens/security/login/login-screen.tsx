import React from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../../../security/login';
import { Button, Card, Form } from 'react-bootstrap';

/**
 * Interface for the validation errors in the login form.
 */
export interface LoginValidationErrors {
  /**
   * Error message for the username field.
   */
  username?: string;
  /**
   * Error message for the password field.
   */
  password?: string;
}

/**
 * LoginForm component that renders a login form with username and password fields.
 * It handles user input, validation, and submission of the login request.
 *
 * @returns JSX Element representing the login form.
 */
export default function LoginForm() {
  // State to manage the username field of login form
  const [username, setUsername] = React.useState('');
  // State to manage the password field of login form
  const [password, setPassword] = React.useState('');
  // State to manage error messages during login
  const [error, setError] = React.useState('');
  // State to manage validation errors in the login form
  const [validationErrors, setValidationErrors] = React.useState<LoginValidationErrors>({});
  // Hook to get the navigate function for programmatic navigation
  const navigate = useNavigate();

  /**
   * Function to handle the login process.
   */
  const handleLogin = async () => {
    const errors: LoginValidationErrors = {};

    if (!username.trim()) errors.username = 'Username cannot be empty';
    if (!password.trim()) errors.password = 'Password cannot be empty';

    setValidationErrors(errors);
    if (Object.keys(errors).length > 0) return;

    await login(
      username,
      password,
      () => navigate(0),
      (errorMessage) => setError(errorMessage)
    );
  };

  /**
   * Function to handle the Enter key press on the username field.
   * It prevents the default action and focuses on the password field.
   *
   * @param e The keyboard event triggered by pressing a key.
   */
  const hadleKeyOnUsername = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      document.getElementById('password-form-control')?.focus();
    }
  };

  /**
   * Function to handle the Enter key press on the password field.
   * It prevents the default action and triggers the login process.
   *
   * @param e The keyboard event triggered by pressing a key.
   */
  const hadleKeyOnPassword = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleLogin();
    }
  };

  return (
    <Card className="mt-4 p-4 shadow-sm" style={{ maxWidth: '400px', width: '100%' }}>
      <h3 className="text-center">Login</h3>
      <Form onSubmit={(e) => e.preventDefault()}>
        <Form.Group className="mb-3">
          <Form.Control
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            isInvalid={!!validationErrors.username}
            onKeyDown={hadleKeyOnUsername}
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Control
            id="password-form-control"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            isInvalid={!!validationErrors.password}
            onKeyDown={hadleKeyOnPassword}
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
