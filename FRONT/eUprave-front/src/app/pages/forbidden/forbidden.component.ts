import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-forbidden',
  standalone: false,
  templateUrl: './forbidden.component.html',
  styleUrl: './forbidden.component.scss'
})
export class ForbiddenComponent {

  constructor(private router: Router) {}

  goHome() {
    this.router.navigate(['/home']);
  }
}
