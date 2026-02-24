import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DocumentResponse {
  documentId: number;
  customerId: number;
  documentType: string;
  documentNumber: string;
  fileName: string;
  fileSize: number;
  mimeType: string;
  documentStatus: string;
  rejectionReason: string;
  expiryDate: string;
  verifiedAt: string;
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
export class DocumentService {

  private apiUrl = `${environment.documentApiUrl}/api/v1/documents`;

  constructor(private http: HttpClient) {}

  uploadDocument(
    customerId: number,
    documentType: string,
    documentNumber: string,
    expiryDate: string,
    file: File
  ): Observable<ApiResponse<DocumentResponse>> {
    const formData = new FormData();
    formData.append('customerId', customerId.toString());
    formData.append('documentType', documentType);
    formData.append('documentNumber', documentNumber);
    if (expiryDate) formData.append('expiryDate', expiryDate);
    formData.append('file', file);

    return this.http.post<ApiResponse<DocumentResponse>>(this.apiUrl, formData);
  }

  getDocumentsByCustomerId(customerId: number): Observable<ApiResponse<DocumentResponse[]>> {
    return this.http.get<ApiResponse<DocumentResponse[]>>(
      `${this.apiUrl}/customer/${customerId}`);
  }

  deleteDocument(documentId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${documentId}`);
  }

  getDocumentsByStatus(status: string): Observable<ApiResponse<DocumentResponse[]>> {
  return this.http.get<ApiResponse<DocumentResponse[]>>(
    `${this.apiUrl}/status/${status}`);
}


verifyDocument(documentId: number, request: any): Observable<ApiResponse<DocumentResponse>> {
  return this.http.patch<ApiResponse<DocumentResponse>>(
    `${this.apiUrl}/${documentId}/verify`, request);
}
}