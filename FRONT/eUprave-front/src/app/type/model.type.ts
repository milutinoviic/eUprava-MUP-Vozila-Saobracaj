export interface CreateOwner {
  firstName: string;
  lastName: string;
  address: string;
  jmbg: string;
  email: string;
}


export interface Owner {
  id: string;
  firstName: string;
  lastName: string;
  address: string;
  jmbg: string;
  email: string;
}

export interface DriverId {
  id: string;
  isSuspended: boolean;
  numberOfViolationPoints: number;
  picture: string;
  owner: Owner;
}

export interface CreateDriverIdRequest {
  ownerJmbg: string;
  picture: File;
}

export interface FineDTO {
  id: string;
  amount: number;
  isPaid: boolean;
  date: string; // LocalDateTime sa backend-a možemo primati kao ISO string
  violationID: string;
}

export interface ViolationDTO {
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

export interface PolicePersonDTO {
  id: string;
  firstName: string;
  lastName: string;
  rank: string;
  suspended: boolean;
  email: string;
}

export interface StatisticDTO {
  date: string;
  numberOfViolations: number;
}

export interface VehicleDto {
  id: string;
  mark: string;
  model: string;
  registration: string;
  year: number;
  color: string;
  isStolen: boolean;
  ownerId: string;
}

export interface OwnerDto {
  id: string;
  firstName: string;
  lastName: string;
  address: string;
  jmbg: string;
  email: string;
}

export interface OwnershipTransferDto {
  id: string;
  vehicle: VehicleDto;
  ownerOld: OwnerDto;
  ownerNew: OwnerDto;
  dateOfTransfer: string; // ISO date string
}

export interface CreateOwnershipTransferDto {
  vehicleId: string;
  oldOwnerId: string;
  newOwnerId: string;
}







