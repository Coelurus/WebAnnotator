import toast from 'react-hot-toast';
import { ErrorResponse } from '../../persistence/errors/error-response';

export default function generateErrorToasts(error: ErrorResponse) {
  try {
    error.response.data.errors.map((error) => {
      toast.error(error.message);
    });
  } catch (error) {
    console.error(error);
  }
}
