import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RoutesService {

  constructor() { }

  // =====================
  // BASE URLs
  // =====================
  private _auth_api_url = 'http://localhost:8000/api/auth';
  private _traffic_api_url = 'http://localhost:8010/api';

  // =====================
  // AUTH ENDPOINTS
  // =====================
  public readonly AUTH_REGISTER = `${this._auth_api_url}/register`;
  public readonly AUTH_LOGIN = `${this._auth_api_url}/login`;
  public readonly AUTH_VERIFY = `${this._auth_api_url}/verify`;

  // =====================
  // FINES
  // =====================
  public readonly FINES_ALL = `${this._traffic_api_url}/fines`;
  public readonly FINES_UPDATE = (id: string) => `${this._traffic_api_url}/fines/${id}`;
  public readonly FINES_UNPAID = (jmbg: string) => `${this._traffic_api_url}/fines/unpaid/${jmbg}`;
  public readonly FINES_VIOLATION = (id: string) => `${this._traffic_api_url}/fines/violation/${id}`;
  // =====================
  // OWNERS
  // =====================
  public readonly OWNERS_ALL = `${this._traffic_api_url}/owners`;
  public readonly OWNERS_HISTORY = (registration: string) =>
    `${this._traffic_api_url}/owners/history/${registration}`;
  public readonly OWNERS_ID = (id: string) => `${this._traffic_api_url}/owners/id/${id}`;
  public readonly OWNERS_BY_ID = (id: string) => `${this._traffic_api_url}/owners/${id}`;
  // =====================
  // POLICE
  // =====================
  public readonly POLICE_ALL = `${this._traffic_api_url}/police`;
  public readonly POLICE_ADD = `${this._traffic_api_url}/police`;
  public readonly POLICE_SUSPEND = (id: string) =>
    `${this._traffic_api_url}/police/suspend/${id}`;
  public readonly POLICE_PROMOTE = (id: string) =>
    `${this._traffic_api_url}/police/promotion/${id}`;
  public readonly POLICE_STATS = (id: string) =>
    `${this._traffic_api_url}/police/statistics/${id}`;
  public readonly POLICE_RANK = (id: string) => `${this._traffic_api_url}/police/rank/${id}`;

  // =====================
  // VEHICLES
  // =====================
  public readonly VEHICLE_STOLEN_CHECK = (registration: string) =>
    `${this._traffic_api_url}/vehicles/stolen/${registration}`;
  public readonly VEHICLE_VERIFY = `${this._traffic_api_url}/vehicles/verify`;
  public readonly VEHICLE_REPORT_STOLEN = (registration: string) =>
    `${this._traffic_api_url}/vehicles/stolen/${registration}`;
  public readonly VEHICLE_SEARCH = `${this._traffic_api_url}/vehicles/search`;
  public readonly VEHICLES_DRIVER = (policeId: string) => `${this._traffic_api_url}/vehicles/driver/${policeId}`;

  // =====================
  // VIOLATIONS
  // =====================
  public readonly VIOLATIONS_ALL = `${this._traffic_api_url}/violations`;
  public readonly VIOLATIONS_BY_POLICE = (policeId: string) =>
    `${this._traffic_api_url}/violations/${policeId}`;
  public readonly VIOLATIONS_BY_DRIVER = (driverId: string) =>
    `${this._traffic_api_url}/violations/history/${driverId}`;
  public readonly VIOLATIONS_EXPORT = (format: string, period: string) =>
    `${this._traffic_api_url}/violations/${format}/${period}`;
  public readonly VIOLATIONS_ADD = `${this._traffic_api_url}/violations`;
  public readonly VIOLATIONS_ASSIGN = (officerId: string, violationId: string) =>
    `${this._traffic_api_url}/violations/assign/${officerId}/${violationId}`;
  public readonly VIOLATIONS_BY_VEHICLE = (vehicleId: string) =>
    `${this._traffic_api_url}/violations/vehicle/history/${vehicleId}`;
}
