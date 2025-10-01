import { Component } from '@angular/core';
import { PoliceService } from '../../service/police.service';
import { FineDTO } from '../../type/model.type';

@Component({
  selector: 'app-fine',
  standalone: false,
  templateUrl: './fine.component.html',
  styleUrl: './fine.component.scss'
})
export class FineComponent {

  fines: FineDTO[] = [];
  driverId: string = '';

  constructor(private policeService: PoliceService) {}

  ngOnInit(): void {}

  fetchFines(): void {
    if (this.driverId) {
      this.policeService.getUnpaidFines(this.driverId).subscribe(data => {
        this.fines = data;
      });
    }
  }

}
