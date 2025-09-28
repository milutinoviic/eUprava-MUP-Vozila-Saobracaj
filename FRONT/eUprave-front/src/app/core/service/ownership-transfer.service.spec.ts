import { TestBed } from '@angular/core/testing';

import { OwnershipTransferService } from './ownership-transfer.service';

describe('OwnershipTransferService', () => {
  let service: OwnershipTransferService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnershipTransferService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
