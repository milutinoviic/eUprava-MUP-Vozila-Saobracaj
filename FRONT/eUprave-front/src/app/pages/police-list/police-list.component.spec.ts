import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoliceListComponent } from './police-list.component';

describe('PoliceListComponent', () => {
  let component: PoliceListComponent;
  let fixture: ComponentFixture<PoliceListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PoliceListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PoliceListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
