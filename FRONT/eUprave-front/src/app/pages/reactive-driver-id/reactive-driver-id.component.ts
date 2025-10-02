import { Component } from '@angular/core';
import {DriverIdService} from '../../service/driver-id.service';
import {DriverIdDto} from '../../type/model.type';

@Component({
  selector: 'app-reactive-driver-id',
  standalone: false,
  templateUrl: './reactive-driver-id.component.html',
  styleUrl: './reactive-driver-id.component.scss'
})
export class ReactiveDriverIdComponent {driverId: string = '';
  driverData?: DriverIdDto;
  errorMessage: string = '';

  constructor(private driverIdService: DriverIdService) {}

  onReactivate() {
    if (!this.driverId) {
      this.errorMessage = 'Unesi ID vozačke dozvole!';
      return;
    }
    this.errorMessage = '';
    this.driverIdService.reactivateDriverId(this.driverId).subscribe({
      next: (data) => {
        this.driverData = data;
      },
      error: (err) => {
        this.errorMessage = 'Greška prilikom reaktivacije!';
        console.error(err);
      }
    });
  }
}
