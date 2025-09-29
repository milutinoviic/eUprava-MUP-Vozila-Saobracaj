import {Component, OnInit} from '@angular/core';
import {Fine, OwnerDTO, OwnerIdDTO, PoliceDTO, VehicleDTO, Violation} from '../../core/type/auth.types';
import {ToastrService} from 'ngx-toastr';
import {OwnerService} from '../../core/service/owner.service';
import {VehicleService} from '../../core/service/vehicle.service';
import {ViolationService} from '../../core/service/violation.service';
import {FineService} from '../../core/service/fine.service';

@Component({
  selector: 'app-owner-list',
  standalone: false,
  templateUrl: './owner-list.component.html',
  styleUrl: './owner-list.component.scss'
})
export class OwnerListComponent implements OnInit{
  owners: OwnerDTO[] = [] ;
  selectedOwner: OwnerDTO | undefined;
  selectedId: OwnerIdDTO | undefined;
  isOn: boolean = true;
   query = '';
  ownersVehicles: VehicleDTO[] = [];
  ownersViolations: Violation[] = [];
  selectedViolation: Violation | undefined;
  selectedFine: Fine | undefined;
  unpaidFines: Fine[] = [];

  constructor(private toastr: ToastrService, private ownerService: OwnerService, private vehicleService: VehicleService, private violationService: ViolationService,
  private fineService: FineService
  ) {
  }

  closeModal() {
    this.selectedOwner = undefined;
    this.isOn = true;
    this.selectedId = undefined;
    this.selectedViolation = undefined;
  }

  showDriverId(owner: OwnerDTO) {
    this.ownerService.getDriverIdByOwner(owner.jmbg).subscribe({
      next: value => {
        this.isOn = false;
        console.log(value);
        this.selectedOwner = owner;
        this.selectedId = value;
      }
    })
  }

  filteredOwners(): OwnerDTO[] {
    const q = this.query.trim().toLowerCase();
    if (!q) return this.owners;
    return this.owners.filter(p =>
      `${p.firstName} ${p.lastName}`.toLowerCase().includes(q) ||
      (p.email?.toLowerCase().includes(q)) ||
      (p.address?.toLowerCase().includes(q)) ||
      (p.jmbg?.includes(q))
    );
  }


  showVehicles(owner: OwnerDTO) {
    this.vehicleService.fetchVehicles(owner.jmbg).subscribe({
      next: value => {
        this.ownersVehicles = value;
        this.isOn = false;
      }
    })

  }

  showViolations(owner: OwnerDTO) {
    const ownerId = owner.id; // or owner.jmbg depending on API
    if (!ownerId) {
      console.warn('Owner ID not found for violations:', owner);
      return;
    }

    this.violationService.fetchViolations(ownerId).subscribe({
      next: (violations: Violation[]) => {
        if (!violations || violations.length === 0) {
          this.toastr.info('No violations found for this owner.');
          return;
        }
        this.ownersViolations = violations;
        this.isOn = false; // hide main cards container
        console.log('Fetched violations:', violations);
      },
      error: (err) => {
        console.error('Error fetching violations:', err);
        this.toastr.error('Failed to fetch violations');
      }
    });
  }


  fetchOwners() {
    this.isOn = true;
    this.ownersVehicles = [];
    this.ownersViolations = [];
    this.ownerService.getAllOwners().subscribe({
      next: data => {
        this.owners = data;

      },
      error: err => {
        this.toastr.error(err.message);
      }
    })
  }

  ngOnInit(): void {
    this.fetchOwners();
  }


  seeFine(viol: Violation) {
    this.fineService.fetchFine(viol.id).subscribe({
      next: value => {
        this.selectedFine = value;
        this.selectedViolation = viol;
      }
    })
  }

  getUnpaidFines(owner: OwnerDTO) {
    const ownerId = owner.id;
    if (!ownerId) {
      console.warn('Owner ID not found for violations:', owner);
      return;
    }

    this.fineService.fetchUnpaidFines(ownerId).subscribe({
      next: (value) => {
        if (!value || value.length === 0) {
          this.toastr.info('No unpaid fines found for this owner.');
          return;
        }
        this.unpaidFines = value;
        this.isOn = false; // hide main cards container
        this.ownersViolations = [];
        console.log('Fetched fines:', value);
      },
      error: (err) => {
        console.error('Error fetching fines:', err);
        this.toastr.error('Failed to fetch fines');
      }
    });
  }
}
