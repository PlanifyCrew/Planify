import {FormsModule} from "@angular/forms";
import {NgModule} from "@angular/core";
import {CalendarComponent} from "./calendar.component";
import {DayPilotModule} from "@daypilot/daypilot-lite-angular";
import {HttpClientModule} from "@angular/common/http";
import {CommonModule} from "@angular/common";

@NgModule({
  imports:      [
    CommonModule,
    FormsModule,
    DayPilotModule,
    HttpClientModule
  ],
  declarations: [],
  exports:      [],
  providers:    [
    // DataService is providedIn: 'root' and HttpClientModule registers HttpClient providers
  ]
})
export class CalendarModule { }
