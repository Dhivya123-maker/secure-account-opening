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
import { MatTableModule } from '@angular/material/table';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../services/auth.service';
import { AccountService, AccountResponse, TransactionResponse } from '../../services/account.service';
import { CustomerService, CustomerResponse } from '../../services/customer.service';

@Component({
  selector: 'app-accounts',
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
    MatTableModule,
    MatDividerModule
  ],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss'
})
export class AccountsComponent implements OnInit {

  currentUser: any;
  customer: CustomerResponse | null = null;
  accounts: AccountResponse[] = [];
  selectedAccount: AccountResponse | null = null;
  transactions: TransactionResponse[] = [];
  isLoading = false;
  isSubmitting = false;
  showCreateForm = false;
  showTransactionForm = false;
  showTransferForm = false;
  successMessage = '';
  errorMessage = '';
  fromDate = '';
  toDate = '';
  today = new Date().toISOString().split('T')[0];
  displayedColumns = ['date', 'type', 'amount', 'balance', 'status'];

  accountForm: FormGroup;
  transactionForm: FormGroup;
  transferForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private customerService: CustomerService,
    private accountService: AccountService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.accountForm = this.fb.group({
      accountType: ['SAVINGS', Validators.required],
      branchCode: ['CHN001'],
      ifscCode: ['SBNK0001234'],
      nomineeName: [''],
      nomineeRelationship: ['']
    });

    this.transactionForm = this.fb.group({
      transactionType: ['CREDIT', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]],
      description: ['']
    });

    this.transferForm = this.fb.group({
      toAccountNumber: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadCustomerAndAccounts();
  }

  loadCustomerAndAccounts(): void {
    this.isLoading = true;
    this.customerService.getAllCustomers().subscribe({
      next: (response) => {
        if (response.success && response.data && response.data.length > 0) {
          this.customer = response.data[0];
          this.loadAccounts(this.customer.customerId);
        } else {
          this.isLoading = false;
          this.router.navigate(['/profile']);
        }
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadAccounts(customerId: number): void {
    this.accountService.getAccountsByCustomerId(customerId).subscribe({
      next: (response) => {
        this.accounts = response.data || [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  createAccount(): void {
    if (this.accountForm.invalid || !this.customer) return;
    this.isSubmitting = true;
    this.errorMessage = '';

    const request = {
      ...this.accountForm.value,
      customerId: this.customer.customerId,
      email: this.customer.email,
      firstName: this.customer.firstName
    };

    this.accountService.createAccount(request).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        if (response.success) {
          this.accounts.push(response.data);
          this.showCreateForm = false;
          this.successMessage = 'Account created successfully!';
          this.cdr.detectChanges();
          setTimeout(() => this.successMessage = '', 3000);
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Failed to create account';
        this.cdr.detectChanges();
        setTimeout(() => this.errorMessage = '', 4000);
      }
    });
  }

  selectAccount(account: AccountResponse): void {
    this.selectedAccount = account;
    this.showTransactionForm = false;
    this.showTransferForm = false;
    this.fromDate = '';
    this.toDate = '';
    this.loadTransactions(account.accountNumber);
  }

  loadTransactions(accountNumber: string): void {
    this.accountService.getTransactionHistory(accountNumber).subscribe({
      next: (response) => {
        if (response.success) {
          this.transactions = response.data;
          this.cdr.detectChanges();
        }
      },
      error: () => {}
    });
  }

  processTransaction(): void {
    if (this.transactionForm.invalid || !this.selectedAccount) return;
    this.isSubmitting = true;
    this.errorMessage = '';

    const request = {
      ...this.transactionForm.value,
      accountNumber: this.selectedAccount.accountNumber
    };

    this.accountService.processTransaction(request).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        if (response.success) {
          this.successMessage = 'Transaction successful!';
          this.showTransactionForm = false;
          this.transactionForm.reset({ transactionType: 'CREDIT' });
          this.loadAccounts(this.customer!.customerId);
          this.loadTransactions(this.selectedAccount!.accountNumber);
          this.cdr.detectChanges();
          setTimeout(() => this.successMessage = '', 3000);
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Transaction failed';
        this.cdr.detectChanges();
        setTimeout(() => this.errorMessage = '', 4000);
      }
    });
  }

  processTransfer(): void {
    if (this.transferForm.invalid || !this.selectedAccount) return;
    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request = {
      ...this.transferForm.value,
      fromAccountNumber: this.selectedAccount.accountNumber
    };

    this.accountService.transfer(request).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        if (response.success) {
          this.successMessage = 'Transfer successful!';
          this.showTransferForm = false;
          this.transferForm.reset();
          this.loadAccounts(this.customer!.customerId);
          this.loadTransactions(this.selectedAccount!.accountNumber);
          this.cdr.detectChanges();
          setTimeout(() => this.successMessage = '', 3000);
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Transfer failed';
        this.cdr.detectChanges();
        setTimeout(() => this.errorMessage = '', 4000);
      }
    });
  }

  filterTransactions(): void {
    if (!this.fromDate || !this.toDate || !this.selectedAccount) return;
    this.accountService.getTransactionsByDateRange(
      this.selectedAccount.accountNumber, this.fromDate, this.toDate
    ).subscribe({
      next: (response) => {
        if (response.success) {
          this.transactions = response.data;
          this.cdr.detectChanges();
        }
      },
      error: () => {}
    });
  }

  clearFilter(): void {
    this.fromDate = '';
    this.toDate = '';
    if (this.selectedAccount) {
      this.loadTransactions(this.selectedAccount.accountNumber);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}