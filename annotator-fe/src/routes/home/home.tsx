import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Button from 'react-bootstrap/Button';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { isUserAdmin, isUserLoggedIn } from '../../security/auth';
import { logout } from '../security/logout/logout';

const HomePage = () => {
  const [showLogin, setShowLogin] = useState(false);
  const [showSignup, setShowSignup] = useState(false);
  const navigate = useNavigate();

  return (
    <>
      <Navbar bg="light" expand="lg" className="shadow-sm">
        <Container>
          <Link to="/">Annotator</Link>
        </Container>
        <Container hidden={!isUserAdmin()}>
          <Link to="/admin/users">Users</Link>
        </Container>
        <Container hidden={!isUserAdmin()}>
          <Link to="/admin/teams">Teams</Link>
        </Container>
        <Container hidden={!isUserLoggedIn()}>
          <Link to="/projects/all">Projects</Link>
        </Container>
        <Container hidden={!isUserLoggedIn()}>
          <Button onClick={() => logout(() => navigate(0))}>Log out</Button>
        </Container>
      </Navbar>

      <Container className="d-flex flex-column align-items-center text-center mt-5">
        <h1 className="fw-bold">Welcome to Annotator</h1>
        <p className="text-muted">Annotate data from capacitive sensor!</p>

        <Row className="mt-4" hidden={isUserLoggedIn()}>
          <Col>
            <Button
              variant="primary"
              size="lg"
              onClick={() => {
                navigate('/user/login');
              }}
            >
              Login
            </Button>
          </Col>
          <Col>
            <Button
              variant="outline-primary"
              size="lg"
              onClick={() => {
                navigate('/user/signup');
              }}
            >
              Sign&nbsp;Up
            </Button>
          </Col>
        </Row>

        <Outlet />
      </Container>
    </>
  );
};

export default HomePage;
