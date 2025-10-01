import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CreateOwnershipTransferDto, OwnershipTransferDto } from '../type/model.type';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OwnershipTransferService {

  private baseUrl = 'http://localhost:8082/api/ownerTransfers';

  constructor(private http: HttpClient) { }

  getOwnershipHistory(registration: string): Observable<OwnershipTransferDto[]> {
    return this.http.get<OwnershipTransferDto[]>(`${this.baseUrl}/getOwnershipTransferForVehicle/${registration}`);
  }

  transferOwnership(dto: CreateOwnershipTransferDto): Observable<OwnershipTransferDto> {
    return this.http.post<OwnershipTransferDto>(`${this.baseUrl}/create/OwnershipTransfer`, dto);
  }
}