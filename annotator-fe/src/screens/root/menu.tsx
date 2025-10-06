// eslint-disable-next-line @typescript-eslint/no-unused-vars
import React from 'react';
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Button from 'react-bootstrap/Button';
import 'bootstrap/dist/css/bootstrap.min.css';
import {Link, Outlet, useNavigate} from 'react-router-dom';
import {isUserAdmin, isUserLoggedIn} from '../../security/auth';
import {logout} from '../../security/logout';
import {Nav} from 'react-bootstrap';
import {Toaster} from 'react-hot-toast';

/**
 * HomePage component that serves as the main layout for the application.
 * It includes a navigation bar and a welcome message.
 *
 * @returns JSX Element representing the home page layout.
 */
export default function Menu() {
    /**
     * Hook to get the navigate function for programmatic navigation.
     */
    const navigate = useNavigate();

    return (
        <>
            <Navbar bg="white" expand="lg" className="shadow-sm">
                <Container>
                    <Navbar.Brand as={Link} to="/home" className="fw-bold fs-4">
                        Annotator
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="navbar-nav"/>
                    <Navbar.Collapse id="navbar-nav">
                        <Nav className="ms-auto">
                            {isUserAdmin() && (
                                <>
                                    <Nav.Link as={Link} to="/admin/users">
                                        Users
                                    </Nav.Link>
                                    <Nav.Link as={Link} to="/admin/teams">
                                        Teams
                                    </Nav.Link>
                                </>
                            )}
                            {isUserLoggedIn() && (
                                <>
                                    <Nav.Link as={Link} to="/projects/all">
                                        Projects
                                    </Nav.Link>
                                    <Button
                                        variant="outline-danger"
                                        size="sm"
                                        onClick={() => logout(() => navigate(0))}
                                        className="ms-3"
                                    >
                                        Log out
                                    </Button>
                                </>
                            )}
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>

            <Toaster position="top-right" reverseOrder={false}/>

            <Container className="d-flex flex-column align-items-center text-center mt-5">
                <Outlet/>
            </Container>
        </>
    );
};
