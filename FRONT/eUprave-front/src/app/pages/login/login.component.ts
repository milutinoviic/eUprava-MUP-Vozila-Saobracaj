import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {Router, RouterLink} from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../core/service/auth.service';
import { AuthRequest } from '../../core/type/auth.types';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, HttpClientModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

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
      this.toastr.warning(this.errorMessage);
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
          this.toastr.success('Success!');
          this.router.navigate(['/home']);
        } else {
          // unlikely, but handle empty token
          this.errorMessage = 'No token!';
          this.toastr.error(this.errorMessage);
        }
      },
      error: (err) => {
        this.loading = false;

        // extract meaningful backend message
        if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else if (err.status === 0) {
          this.errorMessage = 'There is no connection!';
        } else if (err.status === 401) {
          this.errorMessage = 'Wrong credentials!';
        } else {
          this.errorMessage = 'Something went wrong on our side!';
        }

        this.toastr.error(this.errorMessage);
      }
    });
  }
}
