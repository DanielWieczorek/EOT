import { Component, OnInit } from '@angular/core';
import { MachineStatusService } from '../machine-status.service';
import { MachineInfo } from '../machine-info';

@Component({
  selector: 'app-machine-status',
  templateUrl: './machine-status.component.html',
  styleUrls: ['./machine-status.component.css']
})
export class MachineStatusComponent implements OnInit {
    machine: MachineInfo;

 constructor(
    private machineStatusService: MachineStatusService) { }

  getMachine(): void {
    this.machineStatusService.getMachineInfo().then(machine => this.machine = machine);
  }

  ngOnInit(): void {
    this.getMachine();
  }

}
