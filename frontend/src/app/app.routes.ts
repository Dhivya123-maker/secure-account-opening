import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login')
      .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register')
      .then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard')
      .then(m => m.DashboardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile')
      .then(m => m.ProfileComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'accounts',
    loadComponent: () => import('./pages/accounts/accounts')
      .then(m => m.AccountsComponent),
    canActivate: [AuthGuard]
  },
  {
  path: 'documents',
  loadComponent: () => import('./pages/documents/documents')
    .then(m => m.DocumentsComponent),
  canActivate: [AuthGuard]
},
{
  path: 'admin',
  loadComponent: () => import('./pages/admin/admin')
    .then(m => m.AdminComponent),
  canActivate: [AdminGuard]
},
  {
    path: '**',
    redirectTo: 'login'
  }
];