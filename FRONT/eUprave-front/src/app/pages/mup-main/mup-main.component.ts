import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {filter} from 'rxjs/operators';

@Component({
  selector: 'app-mup-main',
  standalone: false,
  templateUrl: './mup-main.component.html',
  styleUrl: './mup-main.component.scss'
})
export class MupMainComponent implements OnInit {
  currentSection:
    | 'create-owner'
    | 'create-driverId'
    | 'unpaidFines'
    | 'vehicleViolations'
    | 'officers'
    | 'statistics'
    | 'ownership-history'
    | 'ownership-transfer'
    | null = null;

  constructor(private router: Router) {}

  private updateFlags(url: string) {
    if (url.includes('/create-owner')) this.currentSection = 'create-owner';
    else if (url.includes('/create-driverId')) this.currentSection = 'create-driverId';
    else if (url.includes('/unpaidFines')) this.currentSection = 'unpaidFines';
    else if (url.includes('/vehicleViolations')) this.currentSection = 'vehicleViolations';
    else if (url.includes('/officers')) this.currentSection = 'officers';
    else if (url.includes('/statistics')) this.currentSection = 'statistics';
    else if (url.includes('/ownership-history')) this.currentSection = 'ownership-history';
    else if (url.includes('/ownership-transfer')) this.currentSection = 'ownership-transfer';
    else this.currentSection = null;
  }

  goBack() {
    this.currentSection = null;
    this.router.navigate(['/home']);
  }

  ngOnInit(): void {
    this.updateFlags(this.router.url);

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.updateFlags(event.urlAfterRedirects);
      });
  }
}
