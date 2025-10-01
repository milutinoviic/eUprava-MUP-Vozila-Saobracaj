import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { LoginGuard } from './core/guard/login.guard';
import { RegistrationComponent } from './pages/registration/registration.component';
import { HomeComponent } from './pages/home/home.component';
import { TrafficPoliceMainComponent } from './pages/traffic-police-main/traffic-police-main.component';
import { PoliceListComponent } from './pages/police-list/police-list.component';
import { CreateOwnerComponent } from './pages/create-owner/create-owner.component';
import { CreateDriverIdComponent } from './pages/create-driver-id/create-driver-id.component';
import { FineComponent } from './pages/fine/fine.component';
import { ViolationsComponent } from './pages/violations/violations.component';
import { OfficersComponent } from './pages/officers/officers.component';
import { StatisticComponent } from './pages/statistic/statistic.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [LoginGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'register', component: RegistrationComponent },
  { path: 'home', component: HomeComponent },
  { path: 'police', component: TrafficPoliceMainComponent },
  { path: 'vehicles', component: TrafficPoliceMainComponent },
  { path: 'fines', component: TrafficPoliceMainComponent },
  { path: 'violations', component: TrafficPoliceMainComponent },
  { path: 'owners', component: TrafficPoliceMainComponent },
  { path: 'police-list', component: PoliceListComponent }, 
  { path: 'create-owner', component: CreateOwnerComponent },
  { path: 'create-driverId', component: CreateDriverIdComponent },
  { path: 'unpaidFines', component: FineComponent },
  { path: 'vehicleViolations', component: ViolationsComponent },
  { path: 'officers', component: OfficersComponent },
  { path: 'statistics', component: StatisticComponent },
  


];

@NgModule({
  imports: [
    RouterModule.forRoot(routes),
    PoliceListComponent 
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
