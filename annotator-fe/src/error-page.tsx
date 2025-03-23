import {useRouteError} from 'react-router-dom';
import {ErrorResponse} from './persistence/errors/error-response';

export default function ErrorPage() {
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
