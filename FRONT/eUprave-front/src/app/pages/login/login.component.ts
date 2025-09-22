import { Component } from '@angular/core';
import { AuthRequest } from '../../core/type/auth.types';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/service/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule,HttpClientModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  loginForm!: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(private fb: FormBuilder, private authService: AuthService,private router:Router) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

    onSubmit(): void {
    if (!this.loginForm.valid) {
      this.loginForm.markAllAsTouched();
      this.errorMessage = 'Forma nije validna!';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const authRequest: AuthRequest = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    this.authService.signIn(authRequest).subscribe({
      next: (res) => {
        this.loading = false;

        if (res.token) {
        
          this.authService.setToken(res.token);
          this.errorMessage = '';
          alert("Login successful!")
          console.log('Login successful!');
          this.router.navigate(["/home"]);
        } else {
        
          console.log(this.errorMessage)
          alert(this.errorMessage)
        }
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Došlo je do greške pri komunikaciji sa serverom!';
      }
    });
  }

}
