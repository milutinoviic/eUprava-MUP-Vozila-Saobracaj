import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficPoliceMainComponent } from './traffic-police-main.component';

describe('TrafficPoliceMainComponent', () => {
  let component: TrafficPoliceMainComponent;
  let fixture: ComponentFixture<TrafficPoliceMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TrafficPoliceMainComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrafficPoliceMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
