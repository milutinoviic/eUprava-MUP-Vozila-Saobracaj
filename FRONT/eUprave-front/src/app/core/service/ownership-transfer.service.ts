import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RoutesService} from './routes.service';
import {Observable} from 'rxjs';
import {OwnershipTransferDTO} from '../type/auth.types';

@Injectable({
  providedIn: 'root'
})
export class OwnershipTransferService {

  constructor(private http: HttpClient, private route: RoutesService) { }


  fetchHistory(req: string): Observable<OwnershipTransferDTO[]> {
    return this.http.get<OwnershipTransferDTO[]>(this.route.OWNERS_HISTORY(req));
  }

}
