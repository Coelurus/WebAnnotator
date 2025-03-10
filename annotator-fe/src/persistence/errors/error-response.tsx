export interface ErrorResponse {
  response: ErrorPayload;
}

interface ErrorPayload {
  data: Error;
}

interface Error {
  status: number;
  errors: ErrorItem[];
  stackTrace: string;
}

interface ErrorItem {
  error: string;
  scope: string;
  message: string;
}
