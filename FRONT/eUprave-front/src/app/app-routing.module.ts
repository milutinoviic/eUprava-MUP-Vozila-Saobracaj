import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { LoginGuard } from './core/guard/login.guard';
import {RegistrationComponent} from './pages/registration/registration.component';
import {HomeComponent} from './pages/home/home.component';
import {TrafficPoliceMainComponent} from './pages/traffic-police-main/traffic-police-main.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent,canActivate:[LoginGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'register', component: RegistrationComponent},
  { path: 'home', component: HomeComponent},
  { path: 'police', component: TrafficPoliceMainComponent},
  { path: 'vehicles', component: TrafficPoliceMainComponent},
  { path: 'fines', component: TrafficPoliceMainComponent},
  { path: 'violations', component: TrafficPoliceMainComponent},
  { path: 'owners', component: TrafficPoliceMainComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
