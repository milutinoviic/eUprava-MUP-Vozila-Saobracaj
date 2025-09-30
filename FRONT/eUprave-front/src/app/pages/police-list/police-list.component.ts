import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RoutesService } from '../../core/service/routes.service';
import { PoliceDTO, Rank, StatisticDTO } from '../../core/type/auth.types';
import { TokenService } from '../../core/utils/token.service';
import { PoliceService } from '../../core/service/police.service';
import { ToastrService } from 'ngx-toastr';
import { ChartConfiguration } from 'chart.js';
import {BaseChartDirective, NgChartsModule} from 'ng2-charts';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-police-list',
  templateUrl: './police-list.component.html',
  standalone: true,
  imports: [
    NgClass,
    FormsModule,
    NgChartsModule,
    NgIf,
    NgForOf,
    // ✅ this is enough
  ],
  styleUrls: ['./police-list.component.scss']
})
export class PoliceListComponent implements OnInit {
  police: PoliceDTO[] = [];
  loading = false;
  error: string | null = null;
  query = '';

  statistics: StatisticDTO[] = [];
  showModal = false;

  // Chart data
  chartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };

  constructor(
    private http: HttpClient,
    private routes: RoutesService,
    private tokenService: TokenService,
    private service: PoliceService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.fetchPolice();
  }

  fetchPolice(): void {
    const loggedInUserId = this.tokenService.getUserId()!.toString();
    this.loading = true;
    this.error = null;

    this.http.get<PoliceDTO[]>(this.routes.POLICE_ALL).subscribe({
      next: data => {
        console.log(data);
        // filter out logged-in user AND incomplete objects
        this.police = (data || []).filter(
          p => p.id && p.id !== loggedInUserId && p.firstName && p.lastName
        );
        this.loading = false;
      },
      error: err => {
        this.error = 'Failed to load officers';
        console.error(err);
        this.loading = false;
      }
    });
  }


  fullName(p: PoliceDTO): string {
    return `${p.firstName} ${p.lastName}`;
  }

  rankLabel(rank: Rank | number): string {
    switch (rank) {
      case Rank.LOW: return 'Officer';
      case Rank.MEDIUM: return 'Sergeant';
      case Rank.HIGH: return 'Inspector';
      default: return 'Unknown';
    }
  }

  rankClass(rank: Rank | number): string {
    switch (rank) {
      case Rank.LOW: return 'rank-low';
      case Rank.MEDIUM: return 'rank-medium';
      case Rank.HIGH: return 'rank-high';
      default: return 'rank-unknown';
    }
  }

  trackById(_: number, item: PoliceDTO) {
    return item.id;
  }

  filteredPolice(): PoliceDTO[] {
    const q = this.query.trim().toLowerCase();
    if (!q) return this.police;
    return this.police.filter(p =>
      `${p.firstName} ${p.lastName}`.toLowerCase().includes(q) ||
      this.rankLabel(p.rank).toLowerCase().includes(q)
    );
  }

  suspendPolice(id: string) {
    this.service.suspendOfficer(id).subscribe({
      next: () => {
        this.toastr.success('Operation successful');
        this.fetchPolice(); // refresh list
      },
      error: (err) => {
        const msg = err?.error?.message || err?.message || 'An unexpected error occurred';
        this.toastr.error(msg);
      }
    });
  }


  promotePolice(id: string) {
    this.service.promoteOfficer(id).subscribe({
      next: () => {
        this.toastr.success("Successfully promoted the officer");
        this.fetchPolice();
      },
      error: err => {
        if (err.status === 403) {
          this.toastr.error("The officer already has the highest rank");
        } else {
          const msg = err?.error?.message || err?.message || 'An unexpected error occurred';
          this.toastr.error(msg);
        }
      }
    });
  }

  seeDailyStatistics(id: string) {
    this.service.statisticsOfficer(id).subscribe({
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

    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }



}
