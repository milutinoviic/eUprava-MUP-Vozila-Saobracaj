import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RoutesService} from './routes.service';
import {Observable} from 'rxjs';
import {OwnerDTO, OwnerIdDTO} from '../type/auth.types';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {

  constructor(private http: HttpClient, private route: RoutesService) { }

  getAllOwners(): Observable<OwnerDTO[]> {
    return this.http.get<OwnerDTO[]>(this.route.OWNERS_ALL);
  }

  getDriverIdByOwner(driverId: string): Observable<OwnerIdDTO> {
    return this.http.get<OwnerIdDTO>(this.route.OWNERS_ID(driverId));
  }
}
