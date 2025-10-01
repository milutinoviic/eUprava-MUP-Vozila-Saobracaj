import { Component } from '@angular/core';
import { PolicePersonDTO } from '../../type/model.type';
import { PoliceService } from '../../service/police.service';

@Component({
  selector: 'app-officers',
  standalone: false,
  templateUrl: './officers.component.html',
  styleUrl: './officers.component.scss'
})
export class OfficersComponent {

  officers: PolicePersonDTO[] = [];

  constructor(private policeService: PoliceService) {}

  ngOnInit(): void {
    this.policeService.getAllOfficers().subscribe(data => {
      this.officers = data;
    });
  }

}
