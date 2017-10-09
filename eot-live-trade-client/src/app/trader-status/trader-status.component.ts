import { Component, OnInit } from '@angular/core';
import { TraderStatusService } from '../trader-status.service';
import { TraderInfo } from '../trader-info';

@Component({
  selector: 'app-trader-status',
  templateUrl: './trader-status.component.html',
  styleUrls: ['./trader-status.component.css']
})
export class TraderStatusComponent implements OnInit {
   trader: TraderInfo;

  constructor(
    private traderStatusService: TraderStatusService) { }

  getTrader(): void {
    this.traderStatusService.getTraderInfo().then(trader => {this.trader = trader;
    console.log('getTrader()' + trader); } );
  }

  ngOnInit(): void {
    this.getTrader();
  }

}
