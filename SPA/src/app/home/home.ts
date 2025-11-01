import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, NgIf} from '@angular/common';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions, EventClickArg } from '@fullcalendar/core';
import { FullCalendarComponent } from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { TaskService } from '../../data/task-service';
import { AddEvent } from './Add-Event/Add-Event';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FullCalendarModule, AddEvent, NgIf],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
  providers: [TaskService]
})

export class HomeComponent {

  
  @ViewChild('calendar') calendarComponent!: FullCalendarComponent;

  Name = 'Planify User';

  showAddEventPopup = false;
  selectedEvent: any | null = null;

 calendarOptions: CalendarOptions = {
  initialView: 'dayGridMonth',
  plugins: [dayGridPlugin, interactionPlugin],
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: 'dayGridMonth,dayGridWeek,dayGridDay'
  },
  dateClick: (info) => {
    alert(`Datum geklickt: ${info.dateStr}`);
    this.showAddEventPopup = true;
  },
  events: [{ title: 'Meeting', date: new Date().toISOString().slice(0, 10) }],

  // Dynamisches Nachladen der Events bei Ansichtwechsel
  datesSet: (info) => {
    this.getEventList(info.start, info.end);
  },

  eventClick: this.handleEventClick.bind(this), // Event-Handler registrieren
};


  constructor(private router: Router, private taskService: TaskService) {}

  ngAfterViewInit() {
    const calendarApi = this.calendarComponent.getApi();
    const view = calendarApi.view; // GridView
    const startDate = view.currentStart; // Startdatum der aktuellen Ansicht
    const endDate = view.currentEnd; // Enddatum der aktuellen Ansicht
    //const month = startDate.getMonth() + 1; // Monat (0-basiert, daher +1)
    //const year = startDate.getFullYear(); // Jahr
    console.log('Current view start date:', startDate);
    this.getEventList(startDate, endDate);
  }


  logout() {
    let token = { token: localStorage.getItem('auth_token') };

    this.taskService.postUserLogOut(token).subscribe(
      data => {
        console.log(data);
      },
      err => console.log('Could not reach server.'),
      () => console.log('Logout complete.')
    );
    localStorage.removeItem('auth_token');
    this.router.navigateByUrl('/auth');
  }


  getEventList(startDate: Date, endDate: Date): void {
    // Hier kann der Token für die Authentifizierung hinzugefügt werden
    const token = localStorage.getItem('auth_token');

    let eventListData = {
      token: token,
      startDate : startDate,
      endDate : endDate
    };

    console.log('Requesting event list with data:', eventListData);

    this.taskService.getEventListData(eventListData).subscribe(
      data => {
        console.log(data);

        const events = data.map((event: any) => {
          let start = event.date;
          let end = event.date;

          if (event.startTime)
            start += 'T' + event.startTime;

          if (event.endTime)
            end += 'T' + event.endTime;

          return {
            title : event.name,
            start : start,
            end : end,
            extendedProps : {
              description : event.description
            }
          };
        });
        //Kalender aktualisieren
        const calendarApi = this.calendarComponent.getApi();
        calendarApi.removeAllEvents();
        calendarApi.addEventSource(events);
      },
      err => console.log('Could not reach server.'),
      () => console.log('Get event list complete.'
    ));
  };

  onClosePopup() {
    this.showAddEventPopup = false;
  }

  handleEventClick(arg: EventClickArg) {
    console.log('Event wurde angeklickt:', arg.event);
    
    const eventData = {
      token: localStorage.getItem('auth_token'),
      event_id: arg.event.id
    }

    this.taskService.getEvent(eventData).subscribe(
      data => { console.log(data)
        this.selectedEvent = data;
        this.showAddEventPopup = true;
      },
      err => console.log("Fehler"),
      () => console.log("Complete")
    );
  }
}