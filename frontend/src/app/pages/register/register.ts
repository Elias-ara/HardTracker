import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {

  name = '';
  email = '';
  password = '';
  loading = false;

  constructor(private http: HttpClient, private router: Router) {}

  fazerCadastro() {
    if (!this.name || !this.email || !this.password) return;

    this.loading = true;

    this.http.post<any>('http://localhost:8080/auth/register', {
      name: this.name,
      email: this.email,
      password: this.password
    }).subscribe({
      next: (response) => {
        localStorage.setItem('auth_token', response.token);
        this.router.navigate(['/dashboard']);
      },
      error: (erro) => {
        alert('Erro ao cadastrar! Verifique os dados.');
        this.loading = false;
      }
    });
  }
}
