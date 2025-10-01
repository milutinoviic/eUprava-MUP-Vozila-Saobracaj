import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FineDTO, PolicePersonDTO, StatisticDTO, ViolationDTO } from '../type/model.type';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PoliceService {


  private baseUrl = 'http://localhost:8082/police';

  constructor(private http: HttpClient) { }

  getUnpaidFines(driverId: string): Observable<FineDTO[]> {
    return this.http.get<FineDTO[]>(`${this.baseUrl}/fines/${driverId}`);
  }

  getVehicleViolations(registration: string): Observable<ViolationDTO[]> {
    return this.http.get<ViolationDTO[]>(`${this.baseUrl}/violations/${registration}`);
  }

  getAllOfficers(): Observable<PolicePersonDTO[]> {
    return this.http.get<PolicePersonDTO[]>(`${this.baseUrl}/officers`);
  }

  getStatistics(policeId: string): Observable<StatisticDTO[]> {
    return this.http.get<StatisticDTO[]>(`${this.baseUrl}/statistics/${policeId}`);
  }
}
