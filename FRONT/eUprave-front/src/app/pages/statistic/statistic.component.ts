import { Component } from '@angular/core';
import { StatisticDTO } from '../../type/model.type';
import { PoliceService } from '../../service/police.service';

@Component({
  selector: 'app-statistic',
  standalone: false,
  templateUrl: './statistic.component.html',
  styleUrl: './statistic.component.scss'
})
export class StatisticComponent {

  statistics: StatisticDTO[] = [];
  policeId: string = '';

  constructor(private policeService: PoliceService) {}

  fetchStatistics(): void {
    if (this.policeId) {
      this.policeService.getStatistics(this.policeId).subscribe(data => {
        this.statistics = data;
      });
    }
  }

}
