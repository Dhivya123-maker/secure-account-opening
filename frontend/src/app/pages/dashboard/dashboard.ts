import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';
import { AccountService, AccountResponse } from '../../services/account.service';
import { CustomerService, CustomerResponse } from '../../services/customer.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatMenuModule,
    MatDividerModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {
  currentUser: any;
  customer: CustomerResponse | null = null;
  accounts: AccountResponse[] = [];
  totalBalance = 0;
  isLoading = false;
  private refreshInterval: any;

  constructor(
    private authService: AuthService,
    private customerService: CustomerService,
    private accountService: AccountService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadCustomerData();
    this.refreshInterval = setInterval(() => {
      this.loadCustomerData();
    }, 30000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  loadCustomerData(): void {
    this.isLoading = true;
    const userId = this.currentUser?.userId;

    if (!userId) {
      this.isLoading = false;
      this.cdr.detectChanges();
      return;
    }

    this.customerService.getCustomerByUserId(userId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.customer = response.data;
          this.loadAccounts(this.customer.customerId);
        } else {
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      },
      error: () => {
        this.isLoading = false;
        this.customer = null;
        this.cdr.detectChanges();
      }
    });
  }

  loadAccounts(customerId: number): void {
    this.accountService.getAccountsByCustomerId(customerId).subscribe({
      next: (response) => {
        if (response.success) {
          this.accounts = response.data;
          this.totalBalance = this.accounts.reduce(
            (sum, acc) => sum + Number(acc.balance), 0);
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}