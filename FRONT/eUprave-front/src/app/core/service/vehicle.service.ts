import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RoutesService} from './routes.service';
import {Observable} from 'rxjs';
import {VehicleDTO} from '../type/auth.types';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {

  constructor(private http: HttpClient, private route: RoutesService) { }


  fetchVehicles(driverId: string): Observable<VehicleDTO[]> {
    return this.http.get<VehicleDTO[]>(this.route.VEHICLES_DRIVER(driverId));
  }

}
