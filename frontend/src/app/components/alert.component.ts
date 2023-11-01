import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';

import {AlertService} from 'src/app/_services';
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'alert-component',
  templateUrl: 'alert.component.html',
  imports: [
    NgClass,
    NgIf
  ],
  standalone: true
})
export class AlertComponent implements OnInit, OnDestroy {

  private subscription!: Subscription;
  alert: any;
  timeoutId?: number;

  constructor(private alertService: AlertService) {
  }

  ngOnInit() {
    this.subscription = this.alertService.onAlert()
      .subscribe(alert => {
        switch (alert?.type) {
          case 'success':
            alert.cssClass = 'alert-success';
            break;
          case 'error':
            alert.cssClass = 'alert-danger';
            break;
        }
        this.alert = alert;
        window.clearTimeout(this.timeoutId);
        if (this.alert) {
          this.timeoutId = window.setTimeout(() => {
            this.clear();
          }, 2500);
        }
      });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  clear() {
    this.alertService.clear();
  }

}
