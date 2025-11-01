
import { TaskService } from '../../../data/task-service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgIf } from '@angular/common';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-Add-Event',
  standalone: true,
  imports: [FormsModule, HttpClientModule, NgIf],
  templateUrl: './Add-Event.html',
  styleUrls: ['./Add-Event.css'],
  providers: [TaskService]
})

export class AddEvent implements OnInit {
  @Input() visible: boolean = false;
  @Input() eventData: any | null = null;
  @Output() closed = new EventEmitter<void>();

  titel: string = '';
  datum: string = '';
  beschreibung: string = '';
  startZeit: string = '';
  endeZeit: string = '';
  tnListe: string[] = [];
  tn: string = '';

  constructor(private taskService: TaskService) { }
  ngOnInit() {}

  ngOnChanges() {
    if (this.eventData?.event_id) {
      console.log(this.eventData);
      this.titel = this.eventData?.name;
      this.datum = this.eventData?.date;
      this.beschreibung = this.eventData?.description;
      this.startZeit = this.eventData?.startTime;
      this.endeZeit = this.eventData?.endTime;
      this.tnListe = this.eventData?.tnListe.map((tn: { email: string }) => tn.email) || [];
    }
  }


  addEvent(): void {
    alert('Adding event ' + this.titel + ' on ' + this.datum + ' from ' + this.startZeit + ' to ' + this.endeZeit + ' description: ' + this.beschreibung);

    let data = {
      token: localStorage.getItem('auth_token'),
      event: {
        name: this.titel,
        date: this.datum,
        description: this.beschreibung,
        startTime: this.startZeit,
        endTime: this.endeZeit
      },
      tnListe: this.tnListe
    };
    console.log(data);

    this.taskService.postAddEvent(data).subscribe(
      data => {
        console.log(data);
      },
      err => console.log('Could not reach server.'),
      () => console.log('Add event complete.')
    );

    if (this.tnListe.length) {
      let addUser = {
        token: localStorage.getItem('auth_token'),
        event_id: this.eventData?.event_id,
        tnListe: this.tnListe
      }

      // Teilnehmer E-Mail-Benachrichtigung separat einfügen -> bessere Wartbarkeit
      this.taskService.postAddUser(addUser).subscribe(
        data => {
          console.log(data)
        },
        err => {
          console.log('Could not reach heroku.')
        },
        () => console.log('Login complete.')
      );
    }

     this.closed.emit();
  }


  addParticipant(): void {
    alert('Adding user ' + this.tn + ' to event ' + this.titel + ' on ' + this.datum + ' from ' + this.startZeit + ' to ' + this.endeZeit + ' description: ' + this.beschreibung);
    
    if (this.tn.trim()) {
      this.tnListe.push(this.tn.trim());
      this.tn = ''; // Input zurücksetzen
    }
}

 closePopup(): void { 
    this.closed.emit();
 }


  showSuccess = false;
  showError = false;

 showAddUserSuccess(): void {
    this.showSuccess = true;
  }

  showAddUserFailed(): void {
    this.showError = true;
  }


 deleteEvent(): void {
  if (!this.eventData?.event_id)
    this.closePopup();

  let eventData = {
    token: localStorage.getItem('auth_token'),
    event_id: this.eventData.event_id
  }

  this.taskService.deleteEvent(eventData).subscribe(
    data => { console.log(data) },
    err => console.log("Fehler"),
    () => console.log("Löschen erfolgreich")
  )
 }
}
