import {Component, OnInit} from '@angular/core';
import {OwnerDTO, OwnerIdDTO, PoliceDTO, VehicleDTO} from '../../core/type/auth.types';
import {ToastrService} from 'ngx-toastr';
import {OwnerService} from '../../core/service/owner.service';
import {VehicleService} from '../../core/service/vehicle.service';

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

  constructor(private toastr: ToastrService, private ownerService: OwnerService, private vehicleService: VehicleService) {
  }

  closeModal() {
    this.selectedOwner = undefined;
    this.isOn = true;
    this.selectedId = undefined;
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

  }

  fetchOwners() {
    this.isOn = true;
    this.ownersVehicles = [];
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
}
