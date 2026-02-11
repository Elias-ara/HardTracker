import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login-success',
  standalone: true,
  imports: [], // Se precisar de algo visual, importa aqui, mas essa tela é invisível/rápida
  template: '<p>Autenticando...</p>', // Uma mensagem rápida enquanto redireciona
  styles: []
})
export class LoginSuccess implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // 1. Pega o token da URL (?token=XYZ...)
    this.route.queryParams.subscribe(params => {
      const token = params['token'];

      if (token) {
        // 2. Salva no navegador (localStorage)
        localStorage.setItem('auth_token', token);
        console.log("Token capturado com sucesso! 🦅");

        // 3. Redireciona para o Painel Principal (ajuste a rota se não for '/home')
        this.router.navigate(['/dashboard']);
      } else {
        // Se deu ruim e não veio token, volta pro login
        this.router.navigate(['/login']);
      }
    });
  }
}
