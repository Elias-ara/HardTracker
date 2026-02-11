import { Routes } from '@angular/router';
import {Login} from './pages/login/login';
import {LoginSuccess} from './pages/login-success/login-success';
import {Dashboard} from './pages/dashboard/dashboard';

export const routes: Routes = [
  //Rota padrão, joga pro login
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'login-success', component: LoginSuccess },

  {
    path: 'dashboard',
    component: Dashboard,
    // canActivate: [authGuard] // <--- Vamos ativar isso no futuro para bloquear intrusos
  }
];
