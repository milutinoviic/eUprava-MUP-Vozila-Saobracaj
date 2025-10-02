import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {CreateDriverIdRequest, DriverId, DriverIdDto} from '../type/model.type';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DriverIdService {

  private apiUrl = 'http://localhost:8082/api/driverIds';

  constructor(private http: HttpClient) {}

  createDriverId(data: CreateDriverIdRequest): Observable<DriverId> {
    const formData = new FormData();
    formData.append('ownerJmbg', data.ownerJmbg);

    for (const [key, val] of formData.entries()) {
      console.log(key, val);
    }

    return this.http.post<DriverId>(this.apiUrl, formData);
  }

  reactivateDriverId(driverId: string) {
    return this.http.patch<DriverIdDto>(
      `${this.apiUrl}/reactivateDriverId/${driverId}`,
      {} // PATCH usually expects a body, but here it's empty
    );
  }

}
