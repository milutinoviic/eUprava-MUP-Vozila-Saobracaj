import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Rank, StatisticDTO} from '../type/auth.types';
import {RoutesService} from './routes.service';

@Injectable({
  providedIn: 'root'
})
export class PoliceService {

  constructor(private http: HttpClient, private route: RoutesService) { }


  findRank(officerId: string): Observable<Rank> {
    return this.http.get<Rank>(this.route.POLICE_RANK(officerId));
  }

  suspendOfficer(officerId: string): Observable<void> {
    return this.http.patch<void>(this.route.POLICE_SUSPEND(officerId), {});
  }

  promoteOfficer(officerId: string): Observable<void> {
    return this.http.patch<void>(this.route.POLICE_PROMOTE(officerId), {});
  }
  statisticsOfficer(officerId: string): Observable<StatisticDTO[]> {
    return this.http.get<StatisticDTO[]>(this.route.POLICE_STATS(officerId));
  }
}
