import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { AuthService } from '../../services/auth.service';
import { CustomerService, CustomerResponse } from '../../services/customer.service';
import { DocumentService, DocumentResponse } from '../../services/document.service';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatChipsModule
  ],
  templateUrl: './documents.html',
  styleUrl: './documents.scss'
})
export class DocumentsComponent implements OnInit {

  currentUser: any;
  customer: CustomerResponse | null = null;
  documents: DocumentResponse[] = [];
  isLoading = false;
  isSubmitting = false;
  showUploadForm = false;
  successMessage = '';
  errorMessage = '';
  selectedFile: File | null = null;
  selectedFileName = '';

  uploadForm: FormGroup;

  documentTypes = [
    { value: 'AADHAAR', label: 'Aadhaar Card' },
    { value: 'PAN', label: 'PAN Card' },
    { value: 'PASSPORT', label: 'Passport' },
    { value: 'DRIVING_LICENSE', label: 'Driving License' },
    { value: 'VOTER_ID', label: 'Voter ID' },
    { value: 'UTILITY_BILL', label: 'Utility Bill' }
  ];

   documentValidationPatterns: { [key: string]: { pattern: string, placeholder: string, hint: string } } = {
  'AADHAAR': { pattern: '^[0-9]{12}$', placeholder: '123456789012', hint: '12 digit Aadhaar number' },
  'PAN': { pattern: '^[A-Z]{5}[0-9]{4}[A-Z]{1}$', placeholder: 'ABCDE1234F', hint: 'PAN format: ABCDE1234F' },
  'PASSPORT': { pattern: '^[A-Z]{1}[0-9]{7}$', placeholder: 'A1234567', hint: 'Passport format: A1234567' },
  'DRIVING_LICENSE': { pattern: '^[A-Z]{2}[0-9]{13}$', placeholder: 'TN1234567890123', hint: '15 character DL number' },
  'VOTER_ID': { pattern: '^[A-Z]{3}[0-9]{7}$', placeholder: 'ABC1234567', hint: 'Voter ID format: ABC1234567' },
  'UTILITY_BILL': { pattern: '.*', placeholder: 'Bill/Account number', hint: 'Any bill number' }
};

currentHint = '';
currentPlaceholder = 'Enter document number';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private customerService: CustomerService,
    private documentService: DocumentService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.uploadForm = this.fb.group({
      documentType: ['', Validators.required],
      documentNumber: ['', Validators.required],
      expiryDate: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadCustomerAndDocuments();
  }

  loadCustomerAndDocuments(): void {
    this.isLoading = true;
  this.customerService.getCustomerByUserId(this.currentUser?.userId).subscribe({
  next: (response) => {
    if (response.success && response.data) {
      this.customer = response.data;
      this.loadDocuments(this.customer.customerId);
    } else {
      this.isLoading = false;
      this.cdr.detectChanges();
    }
  },
  error: () => {
    this.isLoading = false;
    this.cdr.detectChanges();
  }
});
  }

  loadDocuments(customerId: number): void {
    this.documentService.getDocumentsByCustomerId(customerId).subscribe({
      next: (response) => {
        this.documents = response.data || [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

 onFileSelected(event: any): void {
  const file = event.target.files[0];
  if (file) {
    const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf'];
    if (!allowedTypes.includes(file.type)) {
      this.errorMessage = 'Only JPG, PNG and PDF files are allowed';
      event.target.value = '';
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      this.errorMessage = 'File size must be less than 5MB';
      event.target.value = '';
      return;
    }

    const selectedType = this.uploadForm.get('documentType')?.value;
    if (!selectedType) {
      this.errorMessage = 'Please select document type before uploading file';
      event.target.value = '';
      return;
    }

    const docLabel = this.getDocumentLabel(selectedType);
    const confirmed = confirm(
      `Please confirm: Is this file a "${docLabel}"?\n\nUploading a wrong document will delay your KYC verification.`
    );

    if (!confirmed) {
      event.target.value = '';
      this.selectedFile = null;
      this.selectedFileName = '';
      this.cdr.detectChanges();
      return;
    }

    this.selectedFile = file;
    this.selectedFileName = file.name;
    this.errorMessage = '';
    this.cdr.detectChanges();
  }
}

  uploadDocument(): void {
    if (this.uploadForm.invalid || !this.selectedFile || !this.customer) return;
    this.isSubmitting = true;
    this.errorMessage = '';

    const { documentType, documentNumber, expiryDate } = this.uploadForm.value;

    this.documentService.uploadDocument(
      this.customer.customerId,
      documentType,
      documentNumber,
      expiryDate || '',
      this.selectedFile
    ).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        if (response.success) {
          this.documents.push(response.data);
          this.showUploadForm = false;
          this.uploadForm.reset();
          this.selectedFile = null;
          this.selectedFileName = '';
          this.successMessage = 'Document uploaded successfully! Pending verification.';
          this.cdr.detectChanges();
          setTimeout(() => this.successMessage = '', 4000);
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Upload failed';
        this.cdr.detectChanges();
        setTimeout(() => this.errorMessage = '', 4000);
      }
    });
  }

  deleteDocument(documentId: number): void {
    if (!confirm('Are you sure you want to delete this document?')) return;

    this.documentService.deleteDocument(documentId).subscribe({
      next: () => {
        this.documents = this.documents.filter(d => d.documentId !== documentId);
        this.successMessage = 'Document deleted successfully';
        this.cdr.detectChanges();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Delete failed';
        this.cdr.detectChanges();
      }
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'VERIFIED': return 'verified';
      case 'REJECTED': return 'rejected';
      default: return 'pending';
    }
  }

  getDocumentLabel(type: string): string {
    return this.documentTypes.find(d => d.value === type)?.label || type;
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  onDocumentTypeChange(type: string): void {
  const validation = this.documentValidationPatterns[type];
  if (validation) {
    this.currentHint = validation.hint;
    this.currentPlaceholder = validation.placeholder;
    this.uploadForm.get('documentNumber')?.setValidators([
      Validators.required,
      Validators.pattern(validation.pattern)
    ]);
    this.uploadForm.get('documentNumber')?.reset();
    this.uploadForm.get('documentNumber')?.updateValueAndValidity();
    this.cdr.detectChanges();
  }
}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
 

}