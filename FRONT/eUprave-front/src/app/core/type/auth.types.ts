export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface User {
  id?: string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}

export interface AuthUser {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: Role;
}

enum Role {
  POLICE,
  ADMIN
}

export enum Rank {
  LOW = 'LOW', MEDIUM = 'MEDIUM', HIGH = 'HIGH'
}

export interface PoliceDTO {
  id: string;
  firstName: string;
  lastName: string;
  suspended: boolean;
  rank: Rank;
}

export interface StatisticDTO {
  date: string;
  numberOfViolations: number;
}

export interface OwnerDTO {
  id: string;
  firstName: string;
  lastName: string;
  address: string;
  jmbg: string;
  email: string;
}

export interface OwnerIdDTO {
  id: string;
  suspended: boolean;
  numberOfViolationPoints: number;
  picture: string;
  owner: OwnerDTO;

}

export interface VehicleDTO {
  id: string;
  mark: string;
  model: string;
  registration: string;
  year: number;
  color: string;
  stolen: boolean;
  ownerId: string;

}

export interface Violation {
  id: string;
  typeOfViolation: TypeOfViolation;
  date: string;
  location: string;
  driverId: string;
  vehicleId: string;
  policeId: string;

}

export enum TypeOfViolation {
  MINOR = 'MINOR',
  MAJOR = 'MAJOR',
  CRITICAL = 'CRITICAL'
}

export interface Fine {
  id: string;
  amount: number;
  paid: boolean;
  date: string;
  violationID: string;
}

export interface OwnershipTransferDTO {
  id: string;
  vehicle: VehicleDTO;
  ownerOld: OwnerDTO;
  ownerNew: OwnerDTO;
  dateOfTransfer: string; // JSON gives you string anyway
}
