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
  date: Date;
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
