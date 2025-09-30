import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OwnerService } from '../../service/owner.service';
import { CreateOwner, Owner } from '../../type/model.type';

@Component({
  selector: 'app-create-owner',
  standalone: false,
  templateUrl: './create-owner.component.html',
  styleUrl: './create-owner.component.scss'
})
export class CreateOwnerComponent {

  ownerForm: FormGroup;
  createdOwner?: Owner;
  submitted = false;

  constructor(private fb: FormBuilder, private ownerService: OwnerService) {
    this.ownerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(3)]],
      lastName: ['', [Validators.required, Validators.minLength(3)]],
      address: ['', [Validators.required, Validators.minLength(3)]],
      jmbg: ['', [Validators.required, Validators.minLength(9), Validators.maxLength(9)]],
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onSubmit(): void {
    this.submitted = true;
    if (this.ownerForm.invalid) {
      return;
    }

    const newOwner: CreateOwner = this.ownerForm.value;

    this.ownerService.createOwner(newOwner).subscribe({
      next: (owner) => {
        this.createdOwner = owner;
        this.ownerForm.reset();
        this.submitted = false;
      },
      error: (err) => {
        console.error('Error creating owner:', err);
      }
    });
  }

}
