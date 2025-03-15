import toast from 'react-hot-toast';
import { ErrorResponse } from '../../persistence/errors/error-response';

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
