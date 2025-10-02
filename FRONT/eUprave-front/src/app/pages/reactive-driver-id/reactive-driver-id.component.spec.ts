import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReactiveDriverIdComponent } from './reactive-driver-id.component';

describe('ReactiveDriverIdComponent', () => {
  let component: ReactiveDriverIdComponent;
  let fixture: ComponentFixture<ReactiveDriverIdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReactiveDriverIdComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReactiveDriverIdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
