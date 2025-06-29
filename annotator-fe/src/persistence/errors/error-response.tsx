/**
 * ErrorResponse interface represents the structure of an error response from the API.
 */
export default interface ErrorResponse {
  /**
   * Payload of the error response containing details about the error.
   */
  response: ErrorPayload;
}

/**
 * ErrorPayload interface represents the payload of the error response.
 */
export interface ErrorPayload {
  /**
   * Data containing the error details.
   */
  data: Error;
}

/**
 * Error interface represents the structure of the error object in the error response.
 */
export interface Error {
  /**
   * The HTTP status code of the error response.
   */
  status: number;
  /**
   * An array of error items that provide details about the errors.
   */
  errors: ErrorItem[];
  /**
   * A stack trace of the error, useful for debugging.
   */
  stackTrace: string;
}

/**
 * ErrorItem interface represents an individual error item in the errors array.
 */
export interface ErrorItem {
  /**
   * Name of the error.
   */
  error: string;
  /**
   * The scope of the error indicating origin of the error.
   */
  scope: string;
  /**
   * A message providing details about the error.
   */
  message: string;
}
