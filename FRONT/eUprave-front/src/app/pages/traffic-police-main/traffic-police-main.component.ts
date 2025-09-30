import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-traffic-police-main',
  standalone: false,
  templateUrl: './traffic-police-main.component.html',
  styleUrls: ['./traffic-police-main.component.scss']
})
export class TrafficPoliceMainComponent implements OnInit {
  currentSection: 'police' | 'fines' | 'vehicles' | 'violations' | 'owners' | 'export' | 'statistics' | null = null;

  private updateFlags(url: string) {
    if (url.includes('/police')) this.currentSection = 'police';
    else if (url.includes('/fines')) this.currentSection = 'fines';
    else if (url.includes('/vehicles')) this.currentSection = 'vehicles';
    else if (url.includes('/violations')) this.currentSection = 'violations';
    else if (url.includes('/owners')) this.currentSection = 'owners';
    else if (url.includes('/export')) this.currentSection = 'export';
    else if (url.includes('/statistics')) this.currentSection = 'statistics';
    else this.currentSection = null;
  }


  constructor(private router: Router) {}



  goBack() {
    this.currentSection = null;
    this.router.navigate(['/home']);
  }

  ngOnInit(): void {
    this.updateFlags(this.router.url);

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.updateFlags(event.urlAfterRedirects);
    });
  }
}
