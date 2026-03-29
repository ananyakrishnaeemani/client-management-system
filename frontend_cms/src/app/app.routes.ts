import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ClientFormComponent } from './client-form/client-form.component';
import { ClientDetailsComponent } from './client-details/client-details.component';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: '', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'add', component: ClientFormComponent, canActivate: [authGuard] },
  { path: 'edit/:id', component: ClientFormComponent, canActivate: [authGuard] },
  { path: 'view/:id', component: ClientDetailsComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
