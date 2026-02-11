import {Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import {jwtDecode} from 'jwt-decode';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  userName: string = 'Visitante'; // Valor padrão caso der erro

  constructor(private router: Router) {}

    ngOnInit(): void {
      // 1. Recupera o token salvo
      const token = localStorage.getItem('auth_token');

      if (token) {
        try {
          // 2. Decodifica o token para ler os dados dentro dele
          const decoded: any = jwtDecode(token);

          // 3. Tenta pegar o nome.
          // Se o backend mandou como 'name', usa ele. Se não, tenta 'sub' (email).
          this.userName = decoded.name || decoded.sub || 'Usuário Google';

          console.log('Dados do Token:', decoded); // util para ver o que mais tem lá (foto, email...)
        } catch (error) {
          console.error('Erro ao ler token:', error);
        }
      }
    }

  logout() {
    const confirmacao = confirm('Tem certeza que deseja sair?');
    if (confirmacao) {
      // Limpa o token
      localStorage.removeItem('auth_token');
      // Volta pro login
      this.router.navigate(['/login']);
    }
  }
}
