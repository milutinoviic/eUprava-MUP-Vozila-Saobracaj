import { Component } from '@angular/core';
import { ViolationDTO } from '../../type/model.type';
import { PoliceService } from '../../service/police.service';

@Component({
  selector: 'app-violations',
  standalone: false,
  templateUrl: './violations.component.html',
  styleUrl: './violations.component.scss'
})
export class ViolationsComponent {

  violations: ViolationDTO[] = [];
  registration: string = '';

  constructor(private policeService: PoliceService) {}

  fetchViolations(): void {
    if (this.registration) {
      this.policeService.getVehicleViolations(this.registration).subscribe(data => {
        this.violations = data;
      });
    }
  }

}
