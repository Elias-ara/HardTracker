import { Component } from '@angular/core';
import { ProductListComponent } from './components/product-list/product-list';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ProductListComponent, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})

export class App {
  title = 'Argus';
}
