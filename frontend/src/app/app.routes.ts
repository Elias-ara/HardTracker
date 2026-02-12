import { Routes } from '@angular/router';
import {Login} from './pages/login/login';
import {LoginSuccess} from './pages/login-success/login-success';
import {Dashboard} from './pages/dashboard/dashboard';
import {authGuard} from './guards/auth.guard';
import {Register} from './pages/register/register';

export const routes: Routes = [
  //Rota padrão, joga pro login
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'login-success', component: LoginSuccess },
  { path: 'register', component: Register },

  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [authGuard]
  }
];
