import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent {

  registerForm: FormGroup;
  isLoading = false;
  hidePassword = true;
  errorMessage = '';
  successMessage = '';
  passwordTouched = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8),
        Validators.pattern(/^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])/)]]
    });
  }

  hasUppercase(): boolean {
    const val = this.registerForm.get('password')?.value || '';
    return /[A-Z]/.test(val);
  }

  hasNumber(): boolean {
    const val = this.registerForm.get('password')?.value || '';
    return /[0-9]/.test(val);
  }

  hasSpecial(): boolean {
    const val = this.registerForm.get('password')?.value || '';
    return /[!@#$%^&*]/.test(val);
  }

  onSubmit(): void {
    this.passwordTouched = false;
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.passwordTouched = true;
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.successMessage = 'Registration successful! Redirecting to login...';
          setTimeout(() => this.router.navigate(['/login']), 2000);
        }
      },
 error: (error) => {
  this.isLoading = false;
  const message = error?.error?.message || 'Registration failed';
  this.errorMessage = message;

  // Mark email field as invalid if email already exists
  if (message.toLowerCase().includes('email')) {
    this.registerForm.get('email')?.setErrors({ serverError: true });
  }

  // Mark username field as invalid if username already exists
  if (message.toLowerCase().includes('username')) {
    this.registerForm.get('username')?.setErrors({ serverError: true });
  }
}
    });
  }
}