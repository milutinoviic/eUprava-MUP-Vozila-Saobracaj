import {Component, OnInit} from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';
import {Router} from '@angular/router';
import {TokenService} from '../../core/utils/token.service';
import {PoliceService} from '../../core/service/police.service';
import {Rank} from '../../core/type/auth.types';
import * as console from 'node:console';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit{
  public role: string | null | undefined;
  public rank: Rank | undefined;

  constructor(private router: Router, private token: TokenService, private policeService: PoliceService) {
  }

  checkRole() {
    this.role = this.token.getUserRole();
    if (this.role == 'POLICE') {
      this.policeService.findRank(this.token.getUserId()!.toString()).subscribe({
        next: (result) => {
          this.rank = result;
        },
        error: err => {
          console.log(err);
        }
      })
    }
  }

  ngOnInit(): void {
    this.checkRole();
  }

  protected readonly Rank = Rank;

  goTo(url: string) {
    this.router.navigate([url])
  }
}
