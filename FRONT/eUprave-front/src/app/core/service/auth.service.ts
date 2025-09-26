import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {AuthRequest, AuthResponse, AuthUser} from '../type/auth.types';
import { Observable } from 'rxjs';
import {RoutesService} from './routes.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authUrl = 'http://localhost:8000/api/auth/login';

  constructor(private http: HttpClient, private routes: RoutesService) { }

  signIn(authRequest: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.authUrl, authRequest);
  }

  setToken(token: string) {
    localStorage.setItem('authToken', token);
  }

  logout() {
    localStorage.removeItem('authToken');
  }

  isLoggedIn():boolean {
    return localStorage.getItem("authToken") != null;
  }

  register(authUser: AuthUser): Observable<any> {
    return this.http.post(this.routes.AUTH_REGISTER, authUser);
  }


}
