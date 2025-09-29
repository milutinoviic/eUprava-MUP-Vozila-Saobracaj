import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateOwner, Owner } from '../type/model.type';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {

  private apiUrl = 'http://localhost:8080/api/owners';

  constructor(private http: HttpClient) { }

  createOwner(owner: CreateOwner): Observable<Owner> {
    return this.http.post<Owner>(`${this.apiUrl}/createOwner`, owner);
  }

  getOwners(): Observable<Owner[]> {
    return this.http.get<Owner[]>(this.apiUrl);

  }
}
