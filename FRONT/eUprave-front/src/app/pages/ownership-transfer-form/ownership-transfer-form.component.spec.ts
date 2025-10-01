import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnershipTransferFormComponent } from './ownership-transfer-form.component';

describe('OwnershipTransferFormComponent', () => {
  let component: OwnershipTransferFormComponent;
  let fixture: ComponentFixture<OwnershipTransferFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OwnershipTransferFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OwnershipTransferFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
