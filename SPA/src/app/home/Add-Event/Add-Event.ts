
import { TaskService } from '../../../data/task-service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgIf } from '@angular/common';
import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-Add-Event',
  standalone: true,
  imports: [FormsModule, HttpClientModule, NgIf],
  templateUrl: './Add-Event.html',
  styleUrls: ['./Add-Event.css'],
  providers: [TaskService]
})

export class AddEvent implements OnInit, OnChanges {
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
  addUserEventId: number | undefined; // für Email-Benachrichtigung

  constructor(private taskService: TaskService) { }
  ngOnInit() {}

  ngOnChanges(changes: SimpleChanges): void {
    console.log('Event data changed:', this.eventData);
    if (changes['eventData'] && changes['eventData'].currentValue) {
      this.titel = this.eventData.name;
      this.datum = this.eventData.date;
      this.beschreibung = this.eventData.description;
      this.startZeit = this.eventData.startTime;
      this.endeZeit = this.eventData.endTime;
      this.tnListe = this.eventData.tnListe?.map((tn: { email: string }) => tn.email) || [];
    }
  }


  addEvent(): void {
    const isEdit = !!this.eventData?.event_id;
    alert('Adding event ' + this.titel + ' on ' + this.datum + ' from ' + this.startZeit + ' to ' + this.endeZeit + ' description: ' + this.beschreibung);

    let data: any = {
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

    if (isEdit) {
    // PUT: Event aktualisieren
    data.event_id = this.eventData.event_id;

    this.taskService.putAddEvent(data).subscribe(
      response => {
        console.log('Event aktualisiert:', response);
        this.addUserEventId = this.eventData.event_id;
        this.sendEmails();
        this.eventData = null;
      },
      err => console.log('Fehler beim Aktualisieren.')
    );
    
    } else {
      // POST: Neues Event erstellen
      this.taskService.postAddEvent(data).subscribe(
        data => {
          console.log(data);
          this.addUserEventId = data;
          this.sendEmails();
        },
        err => console.log('Could not reach server.'),
        () => console.log('Add event complete.')
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
  if (!this.eventData?.event_id) {
    this.closePopup();
    return;
  }

  let eventData = {
    token: localStorage.getItem('auth_token'),
    event_id: this.eventData.event_id
  }

  console.log("Löschen mit Daten");
  console.log(eventData);

  this.taskService.deleteEvent(eventData).subscribe(
    data => { console.log(data) },
    err => console.log("Fehler"),
    () => console.log("Löschen erfolgreich")
  )
 }


 sendEmails() {
    if (this.tnListe.length) {
      let addUser = {
        token: localStorage.getItem('auth_token'),
        event: {
          event_id: this.addUserEventId
        },
        tnListe: this.tnListe
      }

      console.log("Emails senden für:");
      console.log(addUser);

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
 }
}
