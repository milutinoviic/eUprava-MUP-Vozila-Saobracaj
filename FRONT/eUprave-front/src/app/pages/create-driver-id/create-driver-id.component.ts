import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CreateDriverIdRequest, DriverId } from '../../type/model.type';
import { DriverIdService } from '../../service/driver-id.service';

@Component({
  selector: 'app-create-driver-id',
  standalone: false,
  templateUrl: './create-driver-id.component.html',
  styleUrl: './create-driver-id.component.scss'
})
export class CreateDriverIdComponent {


  driverIdForm: FormGroup;
  selectedFile: File | null = null;
  createdDriverId: DriverId | null = null;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private driverIdService: DriverIdService) {
    this.driverIdForm = this.fb.group({
      ownerJmbg: ['', Validators.required]
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit() {
    if (this.driverIdForm.invalid) return;

    const request: CreateDriverIdRequest = {
      ownerJmbg: this.driverIdForm.get('ownerJmbg')?.value,
    };

    this.driverIdService.createDriverId(request)
      .subscribe({
        next: (driverId) => {
          this.createdDriverId = driverId;
          this.errorMessage = '';
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'Failed to create driver ID';
        }
      });
  }


}
