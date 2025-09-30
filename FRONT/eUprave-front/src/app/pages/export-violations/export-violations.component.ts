import { Component } from '@angular/core';
import {ViolationService} from '../../core/service/violation.service';

@Component({
  selector: 'app-export-violations',
  standalone: false,
  templateUrl: './export-violations.component.html',
  styleUrl: './export-violations.component.scss'
})
export class ExportViolationsComponent {
  formats = ['csv', 'pdf'];
  periods = [
    { value: 'last7days', label: 'Last 7 Days' },
    { value: 'last30days', label: 'Last 30 Days' },
    { value: 'thisMonth', label: 'This Month' },
    { value: 'last6months', label: 'Last 6 Months' },
    { value: '1year', label: 'Last 1 Year' }
  ];

  selectedFormat = 'csv';
  selectedPeriod = 'last7days';

  constructor(private violationService: ViolationService) {}

  download() {
    this.violationService.exportViolations(this.selectedFormat, this.selectedPeriod)
      .subscribe(blob => {
        const fileName = `violations_${this.selectedPeriod}.${this.selectedFormat}`;
        const a = document.createElement('a');
        const objectUrl = URL.createObjectURL(blob);
        a.href = objectUrl;
        a.download = fileName;
        a.click();
        URL.revokeObjectURL(objectUrl);
      });
  }
}
