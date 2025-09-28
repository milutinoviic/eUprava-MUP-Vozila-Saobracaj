import { Component, OnInit } from '@angular/core';
import { Fine, Rank } from '../../core/type/auth.types';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { TokenService } from '../../core/utils/token.service';
import { PoliceService } from '../../core/service/police.service';
import { FineService } from '../../core/service/fine.service';
import { ViolationService } from '../../core/service/violation.service';

@Component({
  selector: 'app-fine-list',
  standalone: false,
  templateUrl: './fine-list.component.html',
  styleUrls: ['./fine-list.component.scss'] // fixed typo
})
export class FineListComponent implements OnInit {
  isOn: boolean = true;
  fines: Fine[] = [];
  public rank: Rank | undefined;
  public role: string | null | undefined;

  constructor(
    private http: HttpClient,
    private toastr: ToastrService,
    private token: TokenService,
    private policeService: PoliceService,
    private fineService: FineService,
    private violationService: ViolationService
  ) {}

  ngOnInit(): void {
    this.checkRoleAndFetchFines();
  }

  /**
   * Checks user role and rank, then fetches fines if allowed
   */
  checkRoleAndFetchFines(): void {
    this.role = this.token.getUserRole();

    if (this.role === 'POLICE') {
      this.policeService.findRank(this.token.getUserId()!.toString()).subscribe({
        next: (result: Rank) => {
          this.rank = result;
          console.log('User rank:', this.rank);

          // Only fetch fines if rank is HIGH
          if (this.rank === Rank.HIGH) {
            this.fetchFines();
          } else {
            console.warn('User rank is not HIGH, cannot fetch fines.');
          }
        },
        error: (err) => {
          console.error('Error fetching rank:', err);
          this.toastr.error('Failed to determine user rank');
        }
      });
    } else {
      console.warn('User role is not POLICE, cannot fetch fines.');
    }
  }

  /**
   * Fetch all fines (only called if user has HIGH rank)
   */
  private fetchFines(): void {
    console.log('Fetching fines...');
    this.fineService.fetchAllFines().subscribe({
      next: (data: Fine[]) => {
        console.log('Fetched fines:', data);
        this.fines = data;
      },
      error: (err) => {
        console.error('Error fetching fines:', err);
        this.toastr.error(err.message || 'Failed to fetch fines');
      }
    });
  }
}
