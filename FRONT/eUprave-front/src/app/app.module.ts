import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import {ToastrModule} from 'ngx-toastr';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RegistrationComponent } from './pages/registration/registration.component';
import { HomeComponent } from './pages/home/home.component';
import { PoliceListComponent } from './pages/police-list/police-list.component';
import { TrafficPoliceMainComponent } from './pages/traffic-police-main/traffic-police-main.component';
import {BaseChartDirective} from 'ng2-charts';

@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    HomeComponent,
    PoliceListComponent,
    TrafficPoliceMainComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ToastrModule.forRoot(),
    LoginComponent,
    FormsModule,
    ReactiveFormsModule,
    BaseChartDirective
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
