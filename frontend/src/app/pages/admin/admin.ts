import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { AuthService } from '../../services/auth.service';
import { CustomerService, CustomerResponse } from '../../services/customer.service';
import { DocumentService, DocumentResponse } from '../../services/document.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatTableModule,
    MatTabsModule,
    MatChipsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatBadgeModule
  ],
  templateUrl: './admin.html',
  styleUrl: './admin.scss'
})
export class AdminComponent implements OnInit {

  currentUser: any;
  customers: CustomerResponse[] = [];
  pendingDocuments: DocumentResponse[] = [];
  allDocuments: DocumentResponse[] = [];
  isLoadingCustomers = false;
  isLoadingDocuments = false;
  selectedDocument: DocumentResponse | null = null;
  showVerifyForm = false;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  verifyForm: FormGroup;

  customerColumns = ['name', 'email', 'phone', 'kycStatus', 'createdAt'];
  documentColumns = ['customer', 'docType', 'docNumber', 'fileName', 'uploadedAt', 'status', 'actions'];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private customerService: CustomerService,
    private documentService: DocumentService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.verifyForm = this.fb.group({
      status: ['VERIFIED', Validators.required],
      verificationNotes: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadCustomers();
    this.loadPendingDocuments();
  }

  loadCustomers(): void {
    this.isLoadingCustomers = true;
    this.customerService.getAllCustomers().subscribe({
      next: (response) => {
        this.customers = response.data || [];
        this.isLoadingCustomers = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoadingCustomers = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadPendingDocuments(): void {
    this.isLoadingDocuments = true;
    this.documentService.getDocumentsByStatus('PENDING').subscribe({
      next: (response) => {
        this.pendingDocuments = response.data || [];
        this.isLoadingDocuments = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoadingDocuments = false;
        this.cdr.detectChanges();
      }
    });
  }

  openVerifyDialog(doc: DocumentResponse): void {
    this.selectedDocument = doc;
    this.showVerifyForm = true;
    this.verifyForm.reset({ status: 'VERIFIED', verificationNotes: '' });
  }

  verifyDocument(): void {
      console.log('verifyDocument called');
  console.log('form valid:', this.verifyForm.valid);
  console.log('selectedDocument:', this.selectedDocument);
  console.log('form value:', this.verifyForm.value);
  if (this.verifyForm.invalid || !this.selectedDocument)
     return;
  this.isSubmitting = true;

  const customer = this.customers.find(
    c => Number(c.customerId) === Number(this.selectedDocument?.customerId));

  const request = {
    status: this.verifyForm.value.status,
    remarks: this.verifyForm.value.verificationNotes || '',
    verifiedBy: this.currentUser?.username || 'admin',
    email: customer?.email || '',
    firstName: customer?.firstName || ''
  };

  this.documentService.verifyDocument(
    this.selectedDocument.documentId, request
  ).subscribe({
    next: (response) => {
      this.isSubmitting = false;
      if (response.success) {
        // Directly use customerId from document
        const customerId = Number(this.selectedDocument?.customerId);
        console.log('Updating KYC for customerId:', customerId);

        this.customerService.updateKycStatus(
          customerId,
          this.verifyForm.value.status
        ).subscribe({
          next: (res) => {
            console.log('KYC update response:', res);
            this.loadCustomers();
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('KYC update failed:', err);
          }
        });

        this.successMessage = `Document ${request.status.toLowerCase()} successfully!`;
        this.showVerifyForm = false;
        this.selectedDocument = null;
        this.loadPendingDocuments();
        this.cdr.detectChanges();
        setTimeout(() => this.successMessage = '', 3000);
      }
    },
    error: (error) => {
      this.isSubmitting = false;
      this.errorMessage = error.error?.message || 'Verification failed';
      this.cdr.detectChanges();
      setTimeout(() => this.errorMessage = '', 4000);
    }
  });
}

  getKycStatusColor(status: string): string {
    switch (status) {
      case 'VERIFIED': return 'verified';
      case 'REJECTED': return 'rejected';
      default: return 'pending';
    }
  }
  getVerifiedCount(): number {
  return this.customers.filter(c => c.kycStatus === 'VERIFIED').length;
}

  resetKycStatus(customerId: number): void {
    this.customerService.updateKycStatus(customerId, 'PENDING').subscribe({
      next: () => {
        this.successMessage = 'KYC status reset to PENDING';
        this.loadCustomers();
        this.cdr.detectChanges();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {}
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}