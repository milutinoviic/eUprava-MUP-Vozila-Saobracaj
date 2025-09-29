import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RoutesService} from './routes.service';
import {forkJoin, map, Observable} from 'rxjs';
import {Fine, Violation} from '../type/auth.types';

@Injectable({
  providedIn: 'root'
})
export class FineService {

  constructor(private http: HttpClient, private route: RoutesService) { }

  fetchFine(violationId: string): Observable<Fine> {
    return this.http.get<Fine>(this.route.FINES_VIOLATION(violationId));
  }

  fetchAllFines(): Observable<Fine[]> {
    return this.http.get<Fine[]>(this.route.FINES_ALL);
  }

  fetchFinesByPolice(violations: Violation[]): Observable<Fine[]> {
    const requests = violations.map(v =>
      this.http.get<Fine>(this.route.FINES_VIOLATION(v.id))
    );

    return forkJoin(requests);
  }



  fetchUnpaidFines(jmbg: string): Observable<Fine[]> {
    return this.http.get<Fine[]>(this.route.FINES_UNPAID(jmbg));
  }

  markAsPaid(fineId: string): Observable<void> {
    return this.http.patch<void>(this.route.FINES_UPDATE(fineId), {});
  }

}
