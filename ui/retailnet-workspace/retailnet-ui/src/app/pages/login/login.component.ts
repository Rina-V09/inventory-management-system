import { Component, OnInit, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../shared/services/notification.service';
import { AppContextService } from '../../shared/services/app-context.service';

/** sessionStorage keys for OIDC authorization code + PKCE flow */
const PKCE_VERIFIER_KEY = 'retailnet_pkce_verifier';
const OIDC_STATE_KEY = 'retailnet_oidc_state';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly notificationService = inject(NotificationService);
  private readonly appContext = inject(AppContextService);

  /** HTML5 `pattern` string: 3–64 chars, letters, digits, . _ - */
  readonly usernamePattern = '^[a-zA-Z0-9._-]{3,64}$';
  /** Basic email pattern for template-driven `pattern` validation */
  readonly emailPattern = '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$';
  /** At least 8 chars, with one letter and one number */
  readonly passwordStrengthPattern = '^(?=.*[A-Za-z])(?=.*\\d).{8,128}$';

  username = '';
  password = '';
  email = '';
  confirmPassword = '';
  isLoginMode = true;
  isPasswordVisible = false;

  /** Sign-up: true when both passwords are set and do not match (for button + error text). */
  signupPasswordMismatch(): boolean {
    if (!this.confirmPassword.length) {
      return false;
    }
    return this.password !== this.confirmPassword;
  }

  /** Base URL with no trailing slash, e.g. https://auth.example.com */
  private get keycloakUrl(): string {
    return (
      this.appContext.getContext('KEYCLOAK_URL').replace(/\/+$/, '') ||
      'http://localhost:8080'
    );
  }

  private get realm(): string {
    return this.appContext.getContext('KEYCLOAK_REALM') || 'retailnet';
  }

  private get clientId(): string {
    return this.appContext.getContext('KEYCLOAK_CLIENT_ID') || 'retailnet-ui';
  }

  /**
   * Must match a Valid Redirect URI on the Keycloak client. Override per environment in app-context.json.
   * If unset, uses current browser origin + /login (register that URI in Keycloak for each dev port you use).
   */
  private get redirectUri(): string {
    const configured = this.appContext.getContext('OIDC_REDIRECT_URI').trim();
    if (configured) {
      return configured;
    }
    return `${window.location.origin}/login`;
  }

  private get tokenUrl(): string {
    return `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/token`;
  }

  private get authUrl(): string {
    return `${this.keycloakUrl}/realms/${this.realm}/protocol/openid-connect/auth`;
  }

  private get idpHint(): string {
    return this.appContext.getContext('KEYCLOAK_IDP_HINT').trim() || 'google';
  }

  /**
   * Sends the user to Keycloak's "Forgot password" flow.
   * Requires Realm Settings → Login → **Forgot password** enabled in Keycloak.
   */
  openForgotPassword(event?: Event): void {
    event?.preventDefault();
    const redirect = encodeURIComponent(this.redirectUri);
    const url =
      `${this.keycloakUrl}/realms/${this.realm}/login-actions/reset-credentials` +
      `?client_id=${encodeURIComponent(this.clientId)}` +
      `&redirect_uri=${redirect}`;
    window.location.href = url;
  }

  ngOnInit(): void {
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    const error = params.get('error');
    const errorDescription = params.get('error_description');
    const returnedState = params.get('state');

    if (error) {
      this.notificationService.error(
        errorDescription || error,
        'Sign-in failed'
      );
      this.cleanUrl();
      return;
    }

    if (code) {
      const expectedState = sessionStorage.getItem(OIDC_STATE_KEY);
      if (returnedState && expectedState && returnedState !== returnedState) {
        this.notificationService.error('Invalid login state. Please try again.', 'Security');
        sessionStorage.removeItem(OIDC_STATE_KEY);
        sessionStorage.removeItem(PKCE_VERIFIER_KEY);
        this.cleanUrl();
        return;
      }

      void this.exchangeCodeForToken(code).finally(() => {
        sessionStorage.removeItem(OIDC_STATE_KEY);
        this.cleanUrl();
      });
      return;
    }
  }

  private cleanUrl(): void {
    window.history.replaceState({}, document.title, '/login');
  }

  signIn(): void {
    if (!this.username || !this.password) return;

    const body = new URLSearchParams();
    body.set('client_id', this.clientId);
    body.set('username', this.username);
    body.set('password', this.password);
    body.set('grant_type', 'password');

    this.http
      .post(this.tokenUrl, body.toString(), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      })
      .subscribe({
        next: (response: any) => {
          this.handleToken(response.access_token);
        },
        error: () => {
          this.notificationService.error(
            'Invalid username or password',
            'Login Failed'
          );
        },
      });
  }

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  onSignUp(): void {
    if (!this.username || !this.password || !this.email) {
      this.notificationService.warn(
        'Please fill all required fields.',
        'Validation Error'
      );
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.notificationService.error('Passwords do not match.', 'Validation Error');
      return;
    }

    const payload = {
      username: this.username,
      email: this.email,
      password: this.password,
    };

    const registerUrl =
      this.appContext.getContext('INVENTORY_CONTEXT') + 'users/register';

    this.http
      .post(registerUrl, payload, {
        responseType: 'text',
      })
      .subscribe({
        next: () => {
          this.notificationService.success(
            'Account created successfully! You can now log in.',
            'Success'
          );
          this.toggleMode();
        },
        error: (err) => {
          console.error('Registration failed', err);
          this.notificationService.error(
            'Failed to register account. Username or email might already exist.',
            'Registration Failed'
          );
        },
      });
  }

  /**
   * Redirects to Keycloak with Google IdP hint. Uses PKCE (S256) required for public SPA clients.
   */
  async loginWithGoogle(): Promise<void> {
    const redirectEncoded = encodeURIComponent(this.redirectUri);
    const state = this.randomUrlSafeString(32);
    sessionStorage.setItem(OIDC_STATE_KEY, state);

    const verifier = this.generateCodeVerifier();
    const challenge = await this.createCodeChallenge(verifier);
    sessionStorage.setItem(PKCE_VERIFIER_KEY, verifier);

    const url =
      `${this.authUrl}` +
      `?client_id=${encodeURIComponent(this.clientId)}` +
      `&response_type=code` +
      `&scope=${encodeURIComponent('openid email profile')}` +
      `&redirect_uri=${redirectEncoded}` +
      `&state=${encodeURIComponent(state)}` +
      `&code_challenge_method=S256` +
      `&code_challenge=${encodeURIComponent(challenge)}` +
      `&kc_idp_hint=${encodeURIComponent(this.idpHint)}`;

    window.location.href = url;
  }

  private async exchangeCodeForToken(code: string): Promise<void> {
    const codeVerifier = sessionStorage.getItem(PKCE_VERIFIER_KEY);
    sessionStorage.removeItem(PKCE_VERIFIER_KEY);

    if (!codeVerifier) {
      this.notificationService.error(
        'Missing PKCE verifier. Start Google sign-in again.',
        'Login failed'
      );
      return;
    }

    let body = new HttpParams()
      .set('grant_type', 'authorization_code')
      .set('code', code)
      .set('redirect_uri', this.redirectUri)
      .set('client_id', this.clientId)
      .set('code_verifier', codeVerifier);

    this.http
      .post(this.tokenUrl, body.toString(), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      })
      .subscribe({
        next: (response: any) => {
          this.handleToken(response.access_token);
        },
        error: (err) => {
          console.error('Google / OIDC token exchange failed', err);
          this.notificationService.error(
            'Could not complete sign-in. Ensure Keycloak client "retailnet-ui" is public with Standard flow + PKCE enabled, and redirect URI matches.',
            'Login failed'
          );
        },
      });
  }

  private handleToken(accessToken: string): void {
    localStorage.setItem('token', accessToken);
    try {
      const payload = JSON.parse(atob(accessToken.split('.')[1]));
      console.log('User roles:', payload.realm_access?.roles || []);
    } catch {
      /* ignore */
    }
    this.router.navigate(['/dashboard']);
  }

  /** RFC 7636: 43–128 chars from [A-Z] [a-z] [0-9] -._~ */
  private generateCodeVerifier(): string {
    const array = new Uint8Array(32);
    crypto.getRandomValues(array);
    return this.base64UrlEncode(array);
  }

  private async createCodeChallenge(verifier: string): Promise<string> {
    const data = new TextEncoder().encode(verifier);
    const hash = await crypto.subtle.digest('SHA-256', data);
    return this.base64UrlEncode(new Uint8Array(hash));
  }

  private base64UrlEncode(buffer: Uint8Array): string {
    let binary = '';
    for (let i = 0; i < buffer.length; i++) {
      binary += String.fromCharCode(buffer[i]!);
    }
    return btoa(binary)
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '');
  }

  private randomUrlSafeString(byteLength: number): string {
    const bytes = new Uint8Array(byteLength);
    crypto.getRandomValues(bytes);
    return this.base64UrlEncode(bytes);
  }
}
