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
  title: string = '';
  date: string = '';
  description: string = '';
  startTime: string = '';
  endTime: string = '';

  constructor(private taskService: TaskService) { }
  ngOnInit() { }

  addEvent(): void {
    alert('Adding event ' + this.title + ' on ' + this.date + ' from ' + this.startTime + ' to ' + this.endTime + ' description: ' + this.description);

    let event = {
      title: this.title,
      date: this.date,
      description: this.description,
      startTime: this.startTime,
      endTime: this.endTime
    };

    this.taskService.postAddEvent(event).subscribe(
      data => {
        console.log(data);
      },
      err => console.log('Could not reach server.'),
      () => console.log('Add event complete.')
    );
  }
}
