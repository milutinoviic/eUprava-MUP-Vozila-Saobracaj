import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MupMainComponent } from './mup-main.component';

describe('MupMainComponent', () => {
  let component: MupMainComponent;
  let fixture: ComponentFixture<MupMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MupMainComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MupMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
