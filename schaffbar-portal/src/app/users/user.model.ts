export interface User {
  id: string;
  firstName: string;
  lastName: string;
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
