import { Component } from '@angular/core';
import { OwnershipTransferService } from '../../service/ownership-transfer.service';
import { CreateOwnershipTransferDto } from '../../type/model.type';

@Component({
  selector: 'app-ownership-transfer-form',
  standalone: false,
  templateUrl: './ownership-transfer-form.component.html',
  styleUrl: './ownership-transfer-form.component.scss'
})
export class OwnershipTransferFormComponent {


  dto: CreateOwnershipTransferDto = {
    vehicleId: '',
    oldOwnerId: '',
    newOwnerId: ''
  };

  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(private ownershipService: OwnershipTransferService) {}

  submitTransfer(): void {
    this.successMessage = null;
    this.errorMessage = null;

    this.ownershipService.transferOwnership(this.dto)
      .subscribe({
        next: (res) => {
          this.successMessage = `Ownership transferred successfully!`;
        },
        error: (err) => {
          this.errorMessage = 'Error transferring ownership';
        }
      });
  }

}
