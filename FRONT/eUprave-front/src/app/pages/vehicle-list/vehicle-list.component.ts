import { Component } from '@angular/core';
import {OwnershipTransferDTO, Rank, VehicleDTO} from "../../core/type/auth.types";
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {TokenService} from '../../core/utils/token.service';
import {PoliceService} from '../../core/service/police.service';
import {FineService} from '../../core/service/fine.service';
import {ViolationService} from '../../core/service/violation.service';
import {VehicleService} from '../../core/service/vehicle.service';
import {OwnershipTransferService} from '../../core/service/ownership-transfer.service';

@Component({
  selector: 'app-vehicle-list',
  standalone: false,
  templateUrl: './vehicle-list.component.html',
  styleUrl: './vehicle-list.component.scss'
})
export class VehicleListComponent {
  stolen: boolean = false;
  verification: boolean = false;
  searchParam: boolean = false;
  jmbg!: string;
  registration: string = '';
  markOfVehicle: string = '';
  modelOfVehicle: string = '';
  colorOfVehicle: string = '';
  searchedVehicles: VehicleDTO[] = [];
  history: OwnershipTransferDTO[] = [];

  constructor(
    private http: HttpClient,
    private toastr: ToastrService,
    private token: TokenService,
    private policeService: PoliceService,
    private fineService: FineService,
    private violationService: ViolationService,
    private vehicleService: VehicleService,
    private ownershipHistory: OwnershipTransferService,
  ) {}

  turnOnModal(token: string) {
    switch (token) {
      case 'stolen':
        this.stolen = true;
        this.verification = false;
        this.searchParam = false;
        break;
      case 'verify':
        this.verification = true;
        this.stolen = false;
        this.searchParam = false;
        break;
      case 'search':
        this.searchParam = true;
        this.stolen = false;
        this.verification = false;
        break;
    }

  }

  seeAllVehicles() {

  }

  closeModal(token: string) {
    switch (token) {
      case 'stolen':
        this.stolen = false;
        break;
      case 'verification':
        this.verification = false;
        break;
      case 'search':
        this.searchParam = false;
        break;
    }
  }

  sendVerificationRequest(jmbg: string , registration: string) {
    const req = {
      registration: registration,
      jmbg: jmbg,
    }
    console.log(req);
    this.verification = false;
    this.vehicleService.sendVerification(req).subscribe({
      next: value => {
        this.toastr.info(value);
      },
      error: () => {
        this.toastr.error("There has been an error");
      }
    })

  }

  checkIfStolen(registration: string) {
    this.stolen = false;
    this.vehicleService.seeIfStolen(registration).subscribe({
      next: value => {
        this.toastr.info(value);
      },
      error: (err) => {
        this.toastr.error(err.message);
      }
    })
  }

  searchByParams(mark: string, model: string, color: string, registration: string) {
    this.searchParam = false;

    const search = {
      mark: mark.trim() || null,
      model: model.trim() || null,
      color: color.trim() || null,
      registration: registration.trim() || null,
    };

    console.log('Payload sent to server:', search);

    this.vehicleService.searchVehicles(search).subscribe({
      next: value => {
        console.log('Response from server:', value);
        this.searchedVehicles = value;
      },
      error: err => {
        console.error('Error from server:', err);
      }
    });
  }


  reportAsStolen(vehicle: VehicleDTO) {
    this.vehicleService.reportAsStolen(vehicle.registration).subscribe({
      next: () => {
        this.toastr.success("Reported the vehicle as stolen")
        vehicle.stolen = true;
      },
      error: () => this.toastr.error("Error")
    })
  }

  showHistory(vehicle: VehicleDTO) {
      this.ownershipHistory.fetchHistory(vehicle.registration).subscribe({
        next: value => {
          this.history = value;
          console.log(this.history);
          this.searchedVehicles = [];
        }
      })
  }

  newViolation(vehicle: VehicleDTO) {

  }
}
