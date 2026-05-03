import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

/**
 * Global Error Interceptor for handling HTTP response errors.
 * Specifically handles 401 Unauthorized status by clearing the token 
 * and redirecting the user to the login page.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Handle 401 Unauthorized globally
      if (error.status === 401) {
        console.warn('Session expired or unauthorized. Redirecting to login...');
        localStorage.removeItem('token');
        router.navigate(['/login']);
      }

      // Re-throw the error for component-level handling if needed
      return throwError(() => error);
    })
  );
};
