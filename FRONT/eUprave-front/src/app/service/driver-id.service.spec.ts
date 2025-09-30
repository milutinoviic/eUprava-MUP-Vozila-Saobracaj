import { TestBed } from '@angular/core/testing';

import { DriverIdService } from './driver-id.service';

describe('DriverIdService', () => {
  let service: DriverIdService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverIdService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
