import { Component } from '@angular/core';
import {
  OwnerDTO,
  OwnerIdDTO,
  OwnershipTransferDTO,
  TypeOfViolation,
  VehicleDTO,
  Violation
} from "../../core/type/auth.types";
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { TokenService } from '../../core/utils/token.service';
import { PoliceService } from '../../core/service/police.service';
import { FineService } from '../../core/service/fine.service';
import { ViolationService } from '../../core/service/violation.service';
import { VehicleService } from '../../core/service/vehicle.service';
import { OwnershipTransferService } from '../../core/service/ownership-transfer.service';

import { v4 as uuidv4 } from 'uuid';
import {OwnerService} from '../../core/service/owner.service';

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
  violationModal: boolean = false;

  jmbg!: string;
  registration: string = '';
  markOfVehicle: string = '';
  modelOfVehicle: string = '';
  colorOfVehicle: string = '';

  searchedVehicles: VehicleDTO[] = [];
  history: OwnershipTransferDTO[] = [];
  chosenVehicle: VehicleDTO | undefined;

  // Violation form fields
  typeOfViolation: TypeOfViolation | null = null;
  date: string = '';
  location: string = '';
  violationTypes = Object.values(TypeOfViolation);
  checkYourself: boolean = false;

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
  ) {}

  turnOnModal(token: string) {
    switch (token) {
      case 'stolen':
        this.stolen = true;
        this.verification = false;
        this.searchParam = false;
        this.violationModal = false;
        break;
      case 'verify':
        this.verification = true;
        this.stolen = false;
        this.searchParam = false;
        this.violationModal = false;
        break;
      case 'search':
        this.searchParam = true;
        this.stolen = false;
        this.verification = false;
        this.violationModal = false;
        break;
      case 'violation':
        this.violationModal = true;
        this.stolen = false;
        this.verification = false;
        this.searchParam = false;
        break;
    }
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
      case 'violation':
        this.violationModal = false;
        this.resetViolationForm();
        break;
    }
  }

  sendVerificationRequest(jmbg: string , registration: string) {
    const req = { registration, jmbg };
    this.verification = false;
    this.registration = '';
    this.jmbg = '';
    this.vehicleService.sendVerification(req).subscribe({
      next: value => this.toastr.info(value),
      error: () => this.toastr.error("There has been an error")
    });
  }

  checkIfStolen(registration: string) {
    this.stolen = false;
    this.vehicleService.seeIfStolen(registration).subscribe({
      next: value => {
        this.toastr.info(value)
        this.registration = '';
      },
      error: err => this.toastr.error(err.message)
    });
  }

  searchByParams(mark: string, model: string, color: string, registration: string) {
    this.searchParam = false;
    const search = {
      mark: mark.trim() || null,
      model: model.trim() || null,
      color: color.trim() || null,
      registration: registration.trim() || null,
    };

    this.markOfVehicle = '';
    this.modelOfVehicle = '';
    this.colorOfVehicle = '';
    this.registration = '';
    this.vehicleService.searchVehicles(search).subscribe({
      next: value => this.searchedVehicles = value,
      error: err => console.error('Error from server:', err)
    });
  }

  reportAsStolen(vehicle: VehicleDTO) {
    const state = vehicle.stolen;
    this.vehicleService.reportAsStolen(vehicle.registration).subscribe({
      next: () => {
        this.toastr.success("Reported the vehicle as stolen");
        vehicle.stolen = !state;
      },
      error: () => this.toastr.error("Error")
    });
  }

  showHistory(vehicle: VehicleDTO) {
    this.ownershipHistory.fetchHistory(vehicle.registration).subscribe({
      next: value => {
        this.history = value;
        this.searchedVehicles = [];
        this.registration = '';
      }
    });
  }

  newViolationModal(vehicle: VehicleDTO) {
    this.chosenVehicle = vehicle;
    this.turnOnModal('violation');
  }

  createViolation() {
    if (!this.typeOfViolation || !this.date || !this.location || !this.chosenVehicle) {
      this.toastr.error("Please fill all fields");
      return;
    }

    console.log(this.chosenVehicle);

    this.ownerService.getOwnerById(this.chosenVehicle.ownerId).subscribe({
      next: owner => {
        console.log("Owner fetched:", owner);

        this.ownerService.getDriverIdByOwner(owner.jmbg).subscribe({
          next: driverIdObj => {
            console.log("Driver ID fetched:", driverIdObj);

            const violation: Violation = {
              id: crypto.randomUUID(),
              typeOfViolation: this.typeOfViolation!,
              date: this.date,
              location: this.location,
              driverId: this.chosenVehicle!.ownerId,
              vehicleId: this.chosenVehicle!.id,
              policeId: '',
            };

            if (this.checkYourself) {
              violation.policeId = this.token.getUserId()!.toString();
            }

            const payload = { violation, driverId: driverIdObj };
            console.log("Payload prepared:", payload);

            this.violationService.createViolation(payload).subscribe({
              next: () => {
                console.log("Violation created successfully");
                this.toastr.success("Violation created successfully");
                this.closeModal('violation');
                this.resetViolationForm();
              },
              error: err => {
                console.error("Error creating violation:", err);
                this.toastr.error("Error creating violation");
              }
            });
          },
          error: err => {
            console.error("Could not fetch driver ID:", err);
            this.toastr.error("Could not fetch driver ID");
          }
        });
      },
      error: err => {
        console.error("Could not fetch owner:", err);
        this.toastr.error("Could not fetch owner");
      }
    });

  }


  private resetViolationForm() {
    this.typeOfViolation = null;
    this.date = '';
    this.location = '';
    this.chosenVehicle = undefined;
    this.checkYourself = false;
  }
}
