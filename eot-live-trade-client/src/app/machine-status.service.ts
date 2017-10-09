import { Injectable } from '@angular/core';
import { MachineInfo} from './machine-info';

@Injectable()
export class MachineStatusService {

  constructor() { }

  getMachineInfo(): Promise<MachineInfo> {
   const info: MachineInfo = {
     state: 'RUNNING'
    };

    return Promise.resolve(info);
  }

}
