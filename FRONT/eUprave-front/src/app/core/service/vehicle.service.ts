import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RoutesService} from './routes.service';
import {Observable} from 'rxjs';
import {VehicleDTO} from '../type/auth.types';
import {CreateVehicleRequest, VehicleDto} from '../../type/model.type';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {

  constructor(private http: HttpClient, private route: RoutesService) { }


  fetchVehicles(driverId: string): Observable<VehicleDTO[]> {
    return this.http.get<VehicleDTO[]>(this.route.VEHICLES_DRIVER(driverId));
  }

  sendVerification(req: any): Observable<string> {
    return this.http.post(this.route.VEHICLE_VERIFY, req, {
      responseType: 'text'
    });
  }


  seeIfStolen(req: string): Observable<string> {
    return this.http.get(this.route.VEHICLE_STOLEN_CHECK(req), {
      responseType: 'text'
    });
  }
  searchVehicles(req: any): Observable<VehicleDTO[]> {
    return this.http.post<VehicleDTO[]>(this.route.VEHICLE_SEARCH, req);
  }

  reportAsStolen(reg: string): Observable<void> {
    return this.http.post<void>(this.route.VEHICLE_REPORT_STOLEN(reg), {});
  }


  createVehicle(request: CreateVehicleRequest): Observable<VehicleDto> {
    return this.http.post<VehicleDto>(
      this.route.VEHICLES_CREATE,
      request,
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
  }

}
