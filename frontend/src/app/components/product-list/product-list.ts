import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { ProductService } from '../../services/product';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.scss'
})

export class ProductListComponent {

  private productService = inject(ProductService);
  private cd = inject(ChangeDetectorRef); // <--- Ferramenta para forçar atualização da tela

  products: any[] = [];
  newUrl: string = '';
  isLoading = false;

  constructor() {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getProducts().subscribe({
      next: (data: any) => {
        this.products = data;
        this.cd.detectChanges(); // Força a tela a mostrar os produtos
      },
      error: (err) => console.error('Erro ao carregar lista:', err)
    });
  }

  trackProduct() {
    if (!this.newUrl) return;

    console.log('🚀 Enviando:', this.newUrl);
    this.isLoading = true;

    this.productService.trackNewProduct(this.newUrl)
      .pipe(
        finalize(() => {
          // O SEGREDO FINAL:
          console.log('🏁 Finalize rodou: Destravando botão.');
          this.isLoading = false;
          this.newUrl = '';
          this.cd.detectChanges(); // <--- OBRIGA O ANGULAR A DESTRAVAR O BOTÃO AGORA
        })
      )
      .subscribe({
        next: (resp) => {
          console.log('✅ Sucesso:', resp);
          this.loadProducts();
        },
        error: (err) => {
          console.error('⚠️ Erro:', err);
          // Se for status 200, é sucesso disfarçado
          if (err.status === 200) {
            this.loadProducts();
          } else {
            alert('Erro ao processar. Verifique o console.');
          }
        }
      });
  }
  deleteProduct(id: number) {
    if (confirm('Tem certeza que deseja apagar este produto?')) {
      this.productService.deleteProduct(id).subscribe(() => {
        // Recarrega a lista para o item sumir da tela
        this.loadProducts();
      });
    }
  }
}
