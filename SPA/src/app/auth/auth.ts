import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../data/task-service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './auth.html',
  styleUrls: ['./auth.css']
})

export class Auth implements OnInit {
  email: string = '';
  password: string = '';
  private token: any;

  constructor(private taskService: TaskService) { }
  ngOnInit() { }

  login(): void {
    alert('Logging in ' + this.email + ' password ' + this.password);

    let userLogin = {
      email: this.email,
      password : this.password
    }

    this.taskService.postUserLogIn(userLogin).subscribe(
      data => {
        this.token = data;
        console.log(this.token);
        localStorage.setItem('dummy_token', this.token.token);
      },
      err => console.log('Could not reach heroku.'),
      () => console.log('Login complete.')
    );
  }


  signUp(): void {
    //alert('Signing up ' + this.email + ' password ' + this.password);
  }
}
