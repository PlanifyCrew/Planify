import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarComponent } from './calendar/calendar.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, CalendarComponent],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home {
  Name: string = 'User'; // für "Hello, {{ Name }}!"

  // The custom calendar component provides events/config; Home keeps page-level concerns only.

  openAddEvent() {
    console.log('Open Add Event modal');
  }

  logout() {
    console.log('User logged out');
  }
}

