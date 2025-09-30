import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateDriverIdComponent } from './create-driver-id.component';

describe('CreateDriverIdComponent', () => {
  let component: CreateDriverIdComponent;
  let fixture: ComponentFixture<CreateDriverIdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateDriverIdComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateDriverIdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
