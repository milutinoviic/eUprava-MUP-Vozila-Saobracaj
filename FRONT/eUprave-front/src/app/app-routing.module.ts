import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { LoginGuard } from './core/guard/login.guard';
import { RegistrationComponent } from './pages/registration/registration.component';
import { HomeComponent } from './pages/home/home.component';
import { TrafficPoliceMainComponent } from './pages/traffic-police-main/traffic-police-main.component';
import { PoliceListComponent } from './pages/police-list/police-list.component';

import {ExportViolationsComponent} from './pages/export-violations/export-violations.component';
import {DailyStatisticsComponent} from './pages/daily-statistics/daily-statistics.component';

import { CreateOwnerComponent } from './pages/create-owner/create-owner.component';
import { CreateDriverIdComponent } from './pages/create-driver-id/create-driver-id.component';
import { FineComponent } from './pages/fine/fine.component';
import { ViolationsComponent } from './pages/violations/violations.component';
import { OfficersComponent } from './pages/officers/officers.component';
import { StatisticComponent } from './pages/statistic/statistic.component';
import { OwnershipHistoryComponent } from './pages/ownership-history/ownership-history.component';
import { OwnershipTransferFormComponent } from './pages/ownership-transfer-form/ownership-transfer-form.component';
import {CreateVehicleComponent} from './pages/create-vehicle/create-vehicle.component';
import {ReactiveDriverIdComponent} from './pages/reactive-driver-id/reactive-driver-id.component';
import {RoleGuard} from './core/guard/role.guard';
import {ForbiddenComponent} from './pages/forbidden/forbidden.component';


const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [LoginGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'register', component: RegistrationComponent },

  // Routes for POLICE only
  { path: 'police', component: TrafficPoliceMainComponent, canActivate: [RoleGuard], data: { expectedRoles: ['POLICE'] } },
  { path: 'vehicles', component: TrafficPoliceMainComponent, canActivate: [RoleGuard], data: { expectedRoles: ['POLICE'] } },
  { path: 'fines', component: TrafficPoliceMainComponent, canActivate: [RoleGuard], data: { expectedRoles: ['POLICE'] } },
  { path: 'violations', component: TrafficPoliceMainComponent, canActivate: [RoleGuard], data: { expectedRoles: ['POLICE'] } },
  { path: 'owners', component: TrafficPoliceMainComponent, canActivate: [RoleGuard], data: { expectedRoles: ['POLICE'] } },

  // Routes for ADMIN only
  { path: 'create-owner', component: CreateOwnerComponent, canActivate: [RoleGuard], data: { expectedRoles: ['ADMIN'] } },
  { path: 'create-driverId', component: CreateDriverIdComponent, canActivate: [RoleGuard], data: { expectedRoles: ['ADMIN'] } },
  { path: 'createVehicle', component: CreateVehicleComponent, canActivate: [RoleGuard], data: { expectedRoles: ['ADMIN'] } },

  // Routes accessible by both POLICE and ADMIN
  { path: 'reactivate', component: ReactiveDriverIdComponent, canActivate: [RoleGuard], data: { expectedRoles: ['POLICE', 'ADMIN'] } },

  // Open routes
  { path: 'police-list', component: PoliceListComponent },
  { path: 'export', component: ExportViolationsComponent },
  { path: 'statistics', component: DailyStatisticsComponent },
  { path: 'unpaidFines', component: FineComponent },
  { path: 'vehicleViolations', component: ViolationsComponent },
  { path: 'officers', component: OfficersComponent },
  { path: 'owner/statistics', component: StatisticComponent },
  { path: 'ownership-history', component: OwnershipHistoryComponent },
  { path: 'ownership-transfer', component: OwnershipTransferFormComponent },
  { path: 'home', component: HomeComponent },

  // Forbidden page
  { path: 'forbidden', component: ForbiddenComponent },
];


@NgModule({
  imports: [
    RouterModule.forRoot(routes),
    PoliceListComponent
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
