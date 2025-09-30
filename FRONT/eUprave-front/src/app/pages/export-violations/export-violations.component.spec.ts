import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportViolationsComponent } from './export-violations.component';

describe('ExportViolationsComponent', () => {
  let component: ExportViolationsComponent;
  let fixture: ComponentFixture<ExportViolationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExportViolationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExportViolationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
