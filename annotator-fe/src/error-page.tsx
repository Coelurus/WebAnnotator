import {useRouteError} from 'react-router-dom';
import ErrorResponse from './persistence/errors/error-response';

/**
 * ErrorPage component to display error messages when an unexpected error occurs.
 * It uses the useRouteError hook from react-router-dom to access the error details.
 *
 * @returns JSX element representing the error page.
 */
export default function ErrorPage() {
    /**
     * useRouteError hook retrieves the error object from the current route.
     */
    const error = useRouteError() as ErrorResponse;

    return (
        <div id="error-page">
            <h1>Oops!</h1>
            <p>Sorry, an unexpected {error.response.data.status} error has occurred.</p>
            <p>
                {error.response.data.errors.map((error) => (
                    <i key={error.error}>{error.error}</i>
                ))}
            </p>
        </div>
    );
}
