import {Component, OnInit} from '@angular/core';
import {ChartConfiguration, ChartData, DefaultDataPoint} from 'chart.js';
import {StatisticDTO} from '../../core/type/auth.types';
import {HttpClient} from '@angular/common/http';
import {RoutesService} from '../../core/service/routes.service';
import {TokenService} from '../../core/utils/token.service';
import {PoliceService} from '../../core/service/police.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-daily-statistics',
  standalone: false,
  templateUrl: './daily-statistics.component.html',
  styleUrl: './daily-statistics.component.scss'
})
export class DailyStatisticsComponent implements OnInit {
  chartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };
  statistics: StatisticDTO[] = [];
  constructor(
    private http: HttpClient,
    private routes: RoutesService,
    private tokenService: TokenService,
    private service: PoliceService,
    private toastr: ToastrService
  ) {}

  showCharts() {
    this.chartData = {
      labels: this.statistics.map(s => s.date),
      datasets: [
        {
          data: this.statistics.map(s => s.numberOfViolations),
          label: 'Violations',
          fill: true,
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59,130,246,0.2)',
          tension: 0.25
        }
      ]
    };

  }

  ngOnInit(): void {
    this.loadStatistics();
  }

  loadStatistics() {
    const id = this.tokenService.getUserId()?.toString();
    this.service.statisticsOfficer(id!).subscribe({
      next: (value) => {
        this.statistics = value;
        if (this.statistics.length === 0) {
          this.toastr.info("Whoops, no data for this police person");
          return;
        }

        this.showCharts();
      },
      error: (error) => {
        this.toastr.error(error);
      }
    });
  }
}
