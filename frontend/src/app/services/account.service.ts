import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CreateAccountRequest {
  customerId: number;
  accountType: string;
  branchCode?: string;
  ifscCode?: string;
  email?: string;
  firstName?: string;
  nomineeName?: string;
  nomineeRelationship?: string;
}

export interface AccountResponse {
  accountId: number;
  accountNumber: string;
  customerId: number;
  accountType: string;
  accountStatus: string;
  balance: number;
  currency: string;
  branchCode: string;
  ifscCode: string;
  openingDate: string;
  interestRate: number;
  createdAt: string;
}

export interface TransactionRequest {
  accountNumber: string;
  transactionType: string;
  amount: number;
  description?: string;
}

export interface TransactionResponse {
  transactionId: number;
  transactionRef: string;
  accountNumber: string;
  transactionType: string;
  amount: number;
  balanceBefore: number;
  balanceAfter: number;
  description: string;
  status: string;
  transactionDate: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private apiUrl = `${environment.accountApiUrl}/api/v1/accounts`;

  constructor(private http: HttpClient) {}

  createAccount(request: CreateAccountRequest): Observable<ApiResponse<AccountResponse>> {
    return this.http.post<ApiResponse<AccountResponse>>(this.apiUrl, request);
  }

  getAccountsByCustomerId(customerId: number): Observable<ApiResponse<AccountResponse[]>> {
    return this.http.get<ApiResponse<AccountResponse[]>>(
      `${this.apiUrl}/customer/${customerId}`);
  }

  getAccountByNumber(accountNumber: string): Observable<ApiResponse<AccountResponse>> {
    return this.http.get<ApiResponse<AccountResponse>>(
      `${this.apiUrl}/number/${accountNumber}`);
  }

  processTransaction(request: TransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(
      `${this.apiUrl}/transaction`, request);
  }

  getTransactionHistory(accountNumber: string): Observable<ApiResponse<TransactionResponse[]>> {
    return this.http.get<ApiResponse<TransactionResponse[]>>(
      `${this.apiUrl}/${accountNumber}/transactions`);
  }

  transfer(request: any): Observable<ApiResponse<TransactionResponse>> {
  return this.http.post<ApiResponse<TransactionResponse>>(
    `${this.apiUrl}/transfer`, request);
}

getTransactionsByDateRange(accountNumber: string, fromDate: string, toDate: string): Observable<ApiResponse<TransactionResponse[]>> {
  return this.http.get<ApiResponse<TransactionResponse[]>>(
    `${this.apiUrl}/${accountNumber}/transactions/filter?fromDate=${fromDate}&toDate=${toDate}`);
}
}