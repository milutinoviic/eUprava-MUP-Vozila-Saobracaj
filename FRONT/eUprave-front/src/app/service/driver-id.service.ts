import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CreateDriverIdRequest, DriverId } from '../type/model.type';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DriverIdService {

  private apiUrl = 'http://localhost:8080/api/driverIds'; 

  constructor(private http: HttpClient) {}

   createDriverId(data: CreateDriverIdRequest): Observable<DriverId> {
    console.log(data)
    const formData = new FormData();
    formData.append('ownerJmbg', data.ownerJmbg);
    formData.append('picture', data.picture);
     console.log(formData)

    return this.http.post<DriverId>('/api/driver-id', formData);
  }
}
