import { Component } from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {VehicleService} from '../../core/service/vehicle.service';
import {VehicleDto} from '../../type/model.type';
import {JsonPipe, NgIf} from '@angular/common';


@Component({
  selector: 'app-create-vehicle',
  templateUrl: './create-vehicle.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    JsonPipe,
    NgIf
  ],
  styleUrls: ['./create-vehicle.component.scss']
})
export class CreateVehicleComponent {
  createdVehicle?: VehicleDto;
  errorMessage = '';
  vehicleForm;

  constructor(private fb: FormBuilder, private vehicleService: VehicleService) {
    this.vehicleForm = this.fb.group({
      mark: ['', Validators.required],
      model: ['', Validators.required],
      registration: ['', Validators.required],
      year: ['', [Validators.required, Validators.min(1900)]],
      color: ['', Validators.required],
      ownerJmbg: ['', [Validators.required]]
    });
  }

  onSubmit() {
    if (this.vehicleForm.invalid) return;
    console.log(this.vehicleForm.value);
    this.vehicleService.createVehicle(this.vehicleForm.value as any).subscribe({
      next: (vehicle) => {

        this.createdVehicle = vehicle;
        this.errorMessage = '';
        this.vehicleForm.reset();
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = err.error?.message || 'Failed to create vehicle';
      }
    });
  }
}

