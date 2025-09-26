import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ToastrService} from "ngx-toastr";
import {AuthUser, User} from '../../core/type/auth.types';
import {passwordValidator} from '../../core/validation/password-validation';
import {AuthService} from '../../core/service/auth.service';

@Component({
  selector: 'app-registration',
  standalone: false,
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.scss'
})
export class RegistrationComponent implements OnInit {
  registrationForm!: FormGroup;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly toaster: ToastrService,
    private readonly auth: AuthService,
  ){}

  ngOnInit(): void {
    this.registrationForm = this.formBuilder.group({
      email: ['',
        [Validators.required, Validators.email]
      ],
      password: ['',
        [Validators.required, passwordValidator()]
      ],
      fName: ['', [Validators.required]],
      lName: ['', [Validators.required]],
      role: ['', [Validators.required]],
    });
  }

  onSubmit() {
    if (this.registrationForm.valid) {
      const user: AuthUser = {
        email: this.registrationForm.get("email")?.value,
        password: this.registrationForm.get("password")?.value,
        firstName: this.registrationForm.get("fName")?.value,
        lastName: this.registrationForm.get("lName")?.value,
        role: this.registrationForm.get("role")?.value.toUpperCase(), // <-- convert here
      }


      this.auth.register(user).subscribe({
        next: () => {
          this.toaster.success("Successfully registered your account.");
          this.registrationForm.reset();
        },
        error: err => {
          this.toaster.error(err.message, "Error creating the account");
        }
      })
    }
  }
}
