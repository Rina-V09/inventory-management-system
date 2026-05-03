import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Functional HTTP Interceptor for managing authentication tokens.
 * Automatically attaches the Bearer token from localStorage to all 
 * outgoing requests to the backend API.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // Skip adding the token if it's a request to Keycloak or Google
  const isAuthRequest = req.url.includes('/protocol/openid-connect/') || 
                       req.url.includes('google');

  if (token && !isAuthRequest) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  return next(req);
};
