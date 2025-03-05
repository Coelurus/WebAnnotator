import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Import useHistory hook
import { signup } from './signup';
import { Form, Card, Button } from 'react-bootstrap';

interface SignupValidationErrors {
  firstName?: string;
  lastName?: string;
  username?: string;
  password?: string;
}

function SignupForm() {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [validationErrors, setValidationErrors] = useState<SignupValidationErrors>({});
  const navigate = useNavigate();

  const handleSignup = async () => {
    const errors: SignupValidationErrors = {};

    if (!firstName.trim()) errors.firstName = 'First name cannot be empty';
    if (!lastName.trim()) errors.lastName = 'Last name cannot be empty';
    if (!username.trim()) errors.username = 'Username cannot be empty';
    if (!password.trim()) errors.password = 'Password cannot be empty';

    setValidationErrors(errors);
    if (Object.keys(errors).length > 0) return;

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
    <Card className="mt-4 p-4 shadow-sm" style={{ maxWidth: '400px', width: '100%' }}>
      <h3 className="text-center">Sign Up</h3>
      <Form onSubmit={(e) => e.preventDefault()}>
        <Form.Group className="mb-3">
          <Form.Control
            type="text"
            placeholder="First Name"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            isInvalid={!!validationErrors.firstName}
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Control
            type="text"
            placeholder="Last Name"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            isInvalid={!!validationErrors.lastName}
          />
        </Form.Group>
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
        <Button variant="primary" className="w-100" onClick={handleSignup}>
          Sign Up
        </Button>
      </Form>
    </Card>
  );
}

export default SignupForm;
