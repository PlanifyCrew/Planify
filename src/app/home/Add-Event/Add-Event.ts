import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../../data/task-service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-Add-Event',
  standalone: true,
  imports: [FormsModule, HttpClientModule],
  templateUrl: './Add-Event.html',
  styleUrls: ['./Add-Event.css'],
  providers: [TaskService]
})

export class AddEvent implements OnInit {
  titel: string = '';
  datum: string = '';
  beschreibung: string = '';
  startZeit: string = '';
  endeZeit: string = '';
  tnListe: string[] = [];
  tn: string = '';

  constructor(private taskService: TaskService) { }
  ngOnInit() { }

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
  }


  addParticipant(): void {
  if (this.tn.trim()) {
    this.tnListe.push(this.tn.trim());
    this.tn = ''; // Input zurücksetzen
  }
}
}
