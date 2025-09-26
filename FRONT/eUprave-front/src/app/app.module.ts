import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { ToastrModule } from 'ngx-toastr';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RegistrationComponent } from './pages/registration/registration.component';
import { HomeComponent } from './pages/home/home.component';
import { PoliceListComponent } from './pages/police-list/police-list.component';
import { TrafficPoliceMainComponent } from './pages/traffic-police-main/traffic-police-main.component';

// ✅ Import the module, not the directive
import { NgChartsModule } from 'ng2-charts';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    HomeComponent,
    TrafficPoliceMainComponent, // PoliceListComponent removed
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    LoginComponent,
    FormsModule,
    ReactiveFormsModule,
    PoliceListComponent, // ✅ standalone, keep here
    NgChartsModule       // ✅ use module instead of BaseChartDirective
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
