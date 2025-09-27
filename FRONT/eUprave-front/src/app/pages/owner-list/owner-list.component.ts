import { Component } from '@angular/core';
import {OwnerDTO} from '../../core/type/auth.types';

@Component({
  selector: 'app-owner-list',
  standalone: false,
  templateUrl: './owner-list.component.html',
  styleUrl: './owner-list.component.scss'
})
export class OwnerListComponent {
  owners: OwnerDTO[] = [] ;

}
