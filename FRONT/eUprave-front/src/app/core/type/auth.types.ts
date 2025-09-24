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
  LOW, MEDIUM, HIGH
}

export interface PoliceDTO {
  id: string;
  firstName: string;
  lastName: string;
  rank: Rank;
}

export interface StatisticDTO {
  date: Date;
  numberOfViolations: number;
}

