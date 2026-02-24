import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CreateCustomerRequest {
  userId: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  nationality: string;
  panNumber: string;
  aadharNumber: string;
  email: string;
  phone: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  pincode: string;
  country: string;
  employmentType: string;
  employerName?: string;
  designation?: string;
  annualIncome?: number;
  yearsOfExperience?: number;
}

export interface CustomerResponse {
  customerId: number;
  userId: number;
  customerNumber: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  panNumber: string;
  aadharNumber: string;
  kycStatus: string;
  customerStatus: string;
  email: string;
  phone: string;
  city: string;
  state: string;
  pincode: string;
  employmentType: string;
  annualIncome: number;
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

private apiUrl = `${environment.customerApiUrl}/api/v1/customers`;

constructor(private http: HttpClient) {}

  createCustomer(request: CreateCustomerRequest): Observable<ApiResponse<CustomerResponse>> {
    return this.http.post<ApiResponse<CustomerResponse>>(this.apiUrl, request);
  }

  getCustomerByUserId(userId: number): Observable<ApiResponse<CustomerResponse>> {
    return this.http.get<ApiResponse<CustomerResponse>>(`${this.apiUrl}/user/${userId}`);
  }

  getCustomerById(customerId: number): Observable<ApiResponse<CustomerResponse>> {
    return this.http.get<ApiResponse<CustomerResponse>>(`${this.apiUrl}/${customerId}`);
  }

  getAllCustomers(): Observable<ApiResponse<CustomerResponse[]>> {
    return this.http.get<ApiResponse<CustomerResponse[]>>(this.apiUrl);
  }
  updateCustomer(customerId: number, request: any): Observable<ApiResponse<CustomerResponse>> {
  return this.http.put<ApiResponse<CustomerResponse>>(
    `${this.apiUrl}/${customerId}`, request);
}
updateKycStatus(customerId: number, status: string): Observable<ApiResponse<CustomerResponse>> {
  return this.http.patch<ApiResponse<CustomerResponse>>(
    `${this.apiUrl}/${customerId}/kyc-status?status=${status}`, {});
}
}