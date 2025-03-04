import { Container, Navbar, Button, Row, Col, Card } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import { useState } from "react";
import LoginForm from "../security/login/login-screen";
import SignupForm from "../security/signup/signup-screen";

const HomePage = () => {
  const [showLogin, setShowLogin] = useState(false);
  const [showSignup, setShowSignup] = useState(false);

  return (
    <>
      <Navbar bg="light" expand="lg" className="shadow-sm">
        <Container>
          <Navbar.Brand href="#">Annotator</Navbar.Brand>
        </Container>
      </Navbar>

      <Container className="d-flex flex-column align-items-center text-center mt-5">
        <h1 className="fw-bold">Welcome to Annotator</h1>
        <p className="text-muted">Annotate data from capacitive sensor!</p>

        <Row className="mt-4">
          <Col>
            <Button variant="primary" size="lg" onClick={() => { 
                setShowLogin(!showLogin); 
                setShowSignup(false); 
            }}>
              Login
            </Button>
          </Col>
          <Col>
            <Button variant="outline-primary" size="lg" onClick={() => { 
                setShowSignup(!showSignup); 
                setShowLogin(false); 
            }}>
              Sign&nbsp;Up
            </Button>
          </Col>
        </Row>

        {showLogin && <LoginForm />}
        
        {showSignup && <SignupForm />}
      </Container>
    </>
  );
};

export default HomePage;
