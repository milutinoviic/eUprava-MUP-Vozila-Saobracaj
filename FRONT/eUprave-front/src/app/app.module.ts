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
import {AuthInterceptor} from './core/interceptor/auth.interceptor';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import { OwnerListComponent } from './pages/owner-list/owner-list.component';
import { CreateOwnerComponent } from './pages/create-owner/create-owner.component';
import { CreateDriverIdComponent } from './pages/create-driver-id/create-driver-id.component';
import { FineListComponent } from './pages/fine-list/fine-list.component';
import { VehicleListComponent } from './pages/vehicle-list/vehicle-list.component';
import { ViolationListComponent } from './pages/violation-list/violation-list.component';
import { FineComponent } from './pages/fine/fine.component';
import { ViolationsComponent } from './pages/violations/violations.component';
import { OfficersComponent } from './pages/officers/officers.component';
import { StatisticComponent } from './pages/statistic/statistic.component';
import { OwnershipHistoryComponent } from './pages/ownership-history/ownership-history.component';
import { OwnershipTransferFormComponent } from './pages/ownership-transfer-form/ownership-transfer-form.component';

@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    HomeComponent,
    TrafficPoliceMainComponent,
    OwnerListComponent,
    CreateOwnerComponent,
    CreateDriverIdComponent,
    FineListComponent,
    VehicleListComponent,
    ViolationListComponent,
    FineComponent,
    ViolationsComponent,
    OfficersComponent,
    StatisticComponent,
    OwnershipHistoryComponent,
    OwnershipTransferFormComponent
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
  providers: [ { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule {}
