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



