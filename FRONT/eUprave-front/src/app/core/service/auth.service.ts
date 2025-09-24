import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthRequest, AuthResponse } from '../type/auth.types';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authUrl = 'http://localhost:8080/login';

  constructor(private http: HttpClient) { }

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
    if(!localStorage.getItem("authToken")){
      return false;
    }

    return true;
  }


}
