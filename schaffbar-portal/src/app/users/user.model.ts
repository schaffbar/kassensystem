export interface User {
  id: string;
  firstName: string;
  lastName: string;
  dateOfBirth: Date;
  ageGroup: AgeGroup;
  email: string;
  phone: string;
  address: UserAddress;
  createdAt: Date;
  updatedAt: Date;
}

export interface UserAddress {
  addressLine1: string;
  addressLine2: string;
  postalCode: string;
  city: string;
  country: string;
}

export enum AgeGroup {
  Under16 = 'UNDER_16',
  Under18 = 'UNDER_18',
  Adult = 'ADULT',
}
