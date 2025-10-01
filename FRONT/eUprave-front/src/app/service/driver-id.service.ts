import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CreateDriverIdRequest, DriverId } from '../type/model.type';
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
    formData.append('picture', data.picture, data.picture.name); // ✅ ensure file name included

    for (const [key, val] of formData.entries()) {
      console.log(key, val);
    }

    return this.http.post<DriverId>(this.apiUrl, formData);
  }

}
