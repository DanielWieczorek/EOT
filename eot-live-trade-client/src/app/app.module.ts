
import { NgModule } from '@angular/core';
import { TraderStatusService } from './trader-status.service';

import { AppComponent } from './app.component';
import { TraderStatusComponent } from './trader-status/trader-status.component';
import { MachineStatusComponent } from './machine-status/machine-status.component';
import { MachineStatusService } from './machine-status.service';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatButtonModule, MatCardModule, MatMenuModule, MatToolbarModule, MatIconModule, MatListModule,
  MatGridListModule} from '@angular/material';
import {HttpModule} from '@angular/http';

@NgModule({
  declarations: [
    AppComponent,
    TraderStatusComponent,
    MachineStatusComponent
  ],
  imports: [
    BrowserAnimationsModule,
    MatButtonModule, MatCardModule, MatMenuModule, MatToolbarModule, MatIconModule, MatListModule, MatGridListModule,
    HttpModule
  ],
  providers: [TraderStatusService, MachineStatusService],
  bootstrap: [AppComponent]
})
export class AppModule { }
