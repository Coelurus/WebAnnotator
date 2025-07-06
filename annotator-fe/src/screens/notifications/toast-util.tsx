import toast from 'react-hot-toast';
import ErrorResponse from '../../persistence/errors/error-response';

/**
 * Generates error toasts based on the provided error response.
 *
 * @param error The error response containing error messages.
 */
export default function generateErrorToasts(error: ErrorResponse) {
  try {
    error.response.data.errors.map((error) => {
      toast.error(error.message);
    });
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (_) {
    console.error(error);
  }
}
