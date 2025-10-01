import { Component } from '@angular/core';
import { OwnershipTransferDto } from '../../type/model.type';
import { OwnershipTransferService } from '../../service/ownership-transfer.service';

@Component({
  selector: 'app-ownership-history',
  standalone: false,
  templateUrl: './ownership-history.component.html',
  styleUrl: './ownership-history.component.scss'
})
export class OwnershipHistoryComponent {

  registration: string = '';
  history: OwnershipTransferDto[] = [];
  loading: boolean = false;
  error: string | null = null;

  constructor(private ownershipService: OwnershipTransferService) { }

  ngOnInit(): void {}

  getHistory(): void {
    this.loading = true;
    this.error = null;
    this.ownershipService.getOwnershipHistory(this.registration)
      .subscribe({
        next: (data) => {
          this.history = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Error fetching ownership history';
          this.loading = false;
        }
      });
  }

}
