
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private jwtHelper = new JwtHelperService();

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  getDecodedToken(): any | null {
    const token = this.getToken();
    if (!token) return null;
    return this.jwtHelper.decodeToken(token);
  }

  getUserId(): number | null {
    return this.getDecodedToken()?.id?.toString() || null;
  }

  getUserEmail(): string | null {
    return this.getDecodedToken()?.sub || null;
  }

  getUserRole(): string | null {
    return this.getDecodedToken()?.role || null;
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;
    return this.jwtHelper.isTokenExpired(token);
  }
}
