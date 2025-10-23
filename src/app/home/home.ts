import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FullCalendarModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {
  Name = 'Planify User';

  calendarOptions: CalendarOptions = {
    initialView: 'dayGridMonth',
    plugins: [dayGridPlugin, interactionPlugin],
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,dayGridWeek,dayGridDay'
    },
    dateClick: (info) => alert(`Datum geklickt: ${info.dateStr}`),
    events: [{ title: 'Meeting', date: new Date().toISOString().slice(0, 10) }] 
  };
  constructor(private router: Router) {}

  logout() {
    this.router.navigateByUrl('/auth');
  }
} 