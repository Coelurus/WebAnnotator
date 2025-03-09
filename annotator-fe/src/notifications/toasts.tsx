import React from 'react';
import ToastContainer from 'react-bootstrap/esm/ToastContainer';
import Toast from 'react-bootstrap/Toast';

export interface ToastParams{
    header: string,
    body: string,
    variant: string,
}

export function CreateToast(params: ToastParams) {
  return (
    <ToastContainer
          className="p-3"
          position={"top-end"}
          style={{ zIndex: 1 }}
        >
          <Toast 
            delay={3000} 
            autohide
            bg={params.variant}
            className="d-inline-block m-1"
        >
            <Toast.Header closeButton={false}>
              <img
                src="holder.js/20x20?text=%20"
                className="rounded me-2"
                alt=""
              />
              <strong className="me-auto">{params.header}</strong>
            </Toast.Header>
            <Toast.Body className={params.variant === 'dark' ? 'text-white' : ''}>
                {params.body}
            </Toast.Body>
          </Toast>
        </ToastContainer>
  );
}
