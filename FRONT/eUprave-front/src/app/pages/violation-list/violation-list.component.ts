import { Component, OnInit } from '@angular/core';
import { Fine, PoliceDTO, Rank, Violation } from '../../core/type/auth.types';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { TokenService } from '../../core/utils/token.service';
import { PoliceService } from '../../core/service/police.service';
import { FineService } from '../../core/service/fine.service';
import { ViolationService } from '../../core/service/violation.service';
import { VehicleService } from '../../core/service/vehicle.service';
import { OwnershipTransferService } from '../../core/service/ownership-transfer.service';
import { OwnerService } from '../../core/service/owner.service';
import { RoutesService } from '../../core/service/routes.service';


@Component({
  selector: 'app-violation-list',
  standalone: false,
  templateUrl: './violation-list.component.html',
  styleUrls: ['./violation-list.component.scss']
})
export class ViolationListComponent implements OnInit {
  ownersViolations: Violation[] = [];
  selectedFine: Fine | undefined;
  selectedViolationForAssignment: Violation | undefined;
  selectedOfficerId: string | null = null;
  public rank: Rank | undefined;
  police: PoliceDTO[] = [];

  constructor(
    private http: HttpClient,
    private toastr: ToastrService,
    private token: TokenService,
    private policeService: PoliceService,
    private fineService: FineService,
    private violationService: ViolationService,
    private vehicleService: VehicleService,
    private ownershipHistory: OwnershipTransferService,
    private ownerService: OwnerService,
    private routes: RoutesService,
  ) {}

  ngOnInit(): void {
    this.checkRank();
    this.fetchPolice();
  }

  /** Fetch all violations depending on rank */
  fetchViolationsAll(): void {
    if (this.rank !== Rank.LOW) {
      this.violationService.fetchAllViolations().subscribe({
        next: value => {
          this.ownersViolations = value
          console.log(value);
        },
        error: err => this.toastr.error(err),

      });
    } else {
      this.violationService.fetchViolationsByPolice(this.token.getUserId()!.toString()).subscribe({
        next: value => this.ownersViolations = value,
        error: err => this.toastr.error(err),
      });
    }

  }

  /** Check current user rank */
  checkRank(): void {
    this.policeService.findRank(this.token.getUserId()!.toString()).subscribe({
      next: (result: Rank) => {
        this.rank = result;
        this.fetchViolationsAll();

      },
      error: (err) => {
        console.error('Error fetching rank:', err);
        this.toastr.error('Failed to determine user rank');
      }
    });
  }

  /** See fine modal */
  seeFine(viol: Violation) {
    this.fineService.fetchFine(viol.id).subscribe({
      next: value => this.selectedFine = value,
      error: error => this.toastr.error(error),
    });
  }

  /** Close fine modal */
  closeModal() {
    this.selectedFine = undefined;
  }

  /** Mark fine as paid */
  markAsPaid(selectedFine: Fine) {
    this.fineService.markAsPaid(selectedFine.id).subscribe({
      next: () => {
        this.toastr.success("Marked fine as paid");
        this.fetchViolationsAll();
        this.closeModal();
      },
      error: err => this.toastr.error(err),
    });
  }

  /** Open assign officer modal */
  assignToOfficer(viol: Violation) {
    this.selectedViolationForAssignment = viol;
  }

  /** Fetch all police for the dropdown */
  fetchPolice(): void {
    const loggedInUserId = this.token.getUserId()!.toString();

    this.http.get<PoliceDTO[]>(this.routes.POLICE_ALL).subscribe({
      next: data => {
        this.police = (data || []).filter(
          p => p.id && p.id !== loggedInUserId && p.firstName && p.lastName
        );
      },
      error: err => {
        console.log(err);
        this.toastr.error('Failed to fetch police officers');
      }
    });
  }

  /** Assign selected officer to violation */
  assignOfficerToViolation() {
    if (!this.selectedOfficerId || !this.selectedViolationForAssignment) {
      this.toastr.error('Please select an officer');
      return;
    }

    console.log(this.selectedViolationForAssignment.id);

    this.violationService.assignOfficer(this.selectedViolationForAssignment.id, this.selectedOfficerId)
      .subscribe({
        next: () => {
          this.toastr.success('Officer assigned successfully');

          this.closeAssignmentModal();
          this.fetchViolationsAll();
        },
        error: err => this.toastr.error(err),
      });
  }

  /** Close assign officer modal */
  closeAssignmentModal() {
    this.selectedViolationForAssignment = undefined;
    this.selectedOfficerId = null;
  }

  protected readonly Rank = Rank;
}
