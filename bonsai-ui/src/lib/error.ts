import axios from 'axios';

interface ProblemDetailBody {
  detail?: string;
  errors?: Record<string, string>;
}

function formatProblemDetail(data: ProblemDetailBody | undefined, fallback: string): string {
  if (data?.errors && Object.keys(data.errors).length > 0) {
    return Object.entries(data.errors)
      .map(([field, message]) => `${field}: ${message}`)
      .join(' | ');
  }

  if (data?.detail) {
    return data.detail;
  }

  return fallback;
}

export function extractErrorMessage(err: unknown, fallback: string): string {
  if (!axios.isAxiosError(err)) return fallback;

  if (!err.response) {
    return 'Could not connect to the server. Please check your connection.';
  }

  return formatProblemDetail(err.response.data as ProblemDetailBody | undefined, fallback);
}

// For requests with responseType: 'blob' (e.g. file downloads), the error body
// arrives as a Blob instead of already-parsed JSON — must be read manually.
export async function extractBlobErrorMessage(err: unknown, fallback: string): Promise<string> {
  if (!axios.isAxiosError(err)) return fallback;

  if (!err.response) {
    return 'Could not connect to the server. Please check your connection.';
  }

  const data: unknown = err.response.data;
  if (!(data instanceof Blob)) {
    return formatProblemDetail(data as ProblemDetailBody | undefined, fallback);
  }

  try {
    const text = await data.text();
    const parsed = JSON.parse(text) as ProblemDetailBody;
    return formatProblemDetail(parsed, fallback);
  } catch {
    return fallback;
  }
}
