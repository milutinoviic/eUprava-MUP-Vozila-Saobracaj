import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RoutesService} from './routes.service';
import {Observable} from 'rxjs';
import {Violation} from '../type/auth.types';

@Injectable({
  providedIn: 'root'
})
export class ViolationService {

  constructor(private http: HttpClient, private route: RoutesService) { }

  fetchViolations(driverId: string): Observable<Violation[]> {
    return this.http.get<Violation[]>(this.route.VIOLATIONS_BY_DRIVER(driverId));
  }


}
