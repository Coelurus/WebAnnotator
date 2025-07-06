import React from 'react';
import {useNavigate} from 'react-router-dom'; // Import useHistory hook
import {signup} from '../../../security/signup';
import {Button, Card, Form} from 'react-bootstrap';

/**
 * Interface for the validation errors in the signup form.
 */
export interface SignupValidationErrors {
    /**
     * Error message for the first name field.
     */
    firstName?: string;
    /**
     * Error message for the last name field.
     */
    lastName?: string;
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
 * SignupForm component that renders a signup form with first name, last name, username, and password fields.
 * It handles user input, validation, and submission of the signup request.
 *
 * @returns JSX Element representing the signup form.
 */
export default function SignupForm() {
    // State to manage the first name field of signup form
    const [firstName, setFirstName] = React.useState('');
    // State to manage the last name field of signup form
    const [lastName, setLastName] = React.useState('');
    // State to manage the username field of signup form
    const [username, setUsername] = React.useState('');
    // State to manage the password field of signup form
    const [password, setPassword] = React.useState('');
    // State to manage error messages during signup
    const [error, setError] = React.useState('');
    // State to manage validation errors in the signup form
    const [validationErrors, setValidationErrors] = React.useState<SignupValidationErrors>({});
    // Hook to get the navigate function for programmatic navigation
    const navigate = useNavigate();

    /**
     * Function to handle the signup process.
     */
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
        <Card className="mt-4 p-4 shadow-sm" style={{maxWidth: '400px', width: '100%'}}>
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