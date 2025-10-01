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
  type_of_violation: string;
  date: string;
  location: string;
  driverId: string;
  vehicleId: string;
  policeId: string;
}

export interface PolicePersonDTO {
  id: string;
  firstName: string;
  lastName: string;
  rank: string;
  isSuspended: boolean;
  email: string;
}

export interface StatisticDTO {
  date: string;
  numberOfViolations: number;
}







