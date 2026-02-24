import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';
import { CustomerService, CustomerResponse } from '../../services/customer.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class ProfileComponent implements OnInit {

  currentUser: any;
  customer: CustomerResponse | null = null;
  profileForm: FormGroup;
  isLoading = false;
  isSubmitting = false;
  hasProfile = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private customerService: CustomerService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      gender: [''],
      nationality: ['Indian'],
      panNumber: ['', Validators.pattern('[A-Z]{5}[0-9]{4}[A-Z]{1}')],
      aadharNumber: ['', Validators.pattern('[0-9]{12}')],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern('^[6-9]\\d{9}$')]],
      addressLine1: ['', Validators.required],
      addressLine2: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],
      pincode: ['', Validators.pattern('^[1-9][0-9]{5}$')],
      country: ['India'],
      employmentType: ['', Validators.required],
      employerName: [''],
      designation: [''],
      annualIncome: [''],
      yearsOfExperience: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading = true;
    const userId = this.currentUser?.userId;

    if (!userId) {
      this.isLoading = false;
      this.cdr.detectChanges();
      return;
    }

    this.customerService.getCustomerByUserId(userId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success && response.data) {
          this.customer = response.data;
          this.hasProfile = true;
          this.profileForm.patchValue(this.customer);
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) return;
    this.isSubmitting = true;
    this.errorMessage = '';

    const request = {
      ...this.profileForm.value,
      userId: this.currentUser?.userId
    };

    if (this.hasProfile && this.customer) {
      this.customerService.updateCustomer(this.customer.customerId, request).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          if (response.success) {
            this.customer = response.data;
            this.successMessage = 'Profile updated successfully!';
            this.cdr.detectChanges();
            setTimeout(() => this.successMessage = '', 3000);
          }
        },
        error: (error) => {
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'Failed to update profile';
          this.cdr.detectChanges();
          setTimeout(() => this.errorMessage = '', 4000);
        }
      });
    } else {
      this.customerService.createCustomer(request).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          if (response.success) {
            this.customer = response.data;
            this.hasProfile = true;
            this.successMessage = 'Profile created successfully!';
            this.cdr.detectChanges();
            setTimeout(() => {
              this.router.navigate(['/dashboard']);
            }, 1500);
          }
        },
        error: (error) => {
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'Failed to save profile';
          this.cdr.detectChanges();
          setTimeout(() => this.errorMessage = '', 4000);
        }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}