import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private http = inject(HttpClient);

  // URL do Backend
  private apiUrl = 'http://localhost:8080/api/products';

  constructor() { }

  // 1. Enviar URL para rastreio (POST)
  trackNewProduct(url: string): Observable<any> {
    return this.http.post(this.apiUrl + '/track', url);
  }

  // 2. Listar todos os produtos (GET)
  getProducts(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // 3. Deletar um produto (DELETE)
  deleteProduct(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  // 4. Atualizar um produto (PUT)
  updateProduct(product: any) {
    return this.http.put(`${this.apiUrl}/${product.id}`, product);
  }
}
