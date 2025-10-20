import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../data/task-service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [FormsModule, HttpClientModule],
  templateUrl: './auth.html',
  styleUrls: ['./auth.css'],
  providers: [TaskService]
})

export class Auth implements OnInit {
  name: string = '';
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
    alert('Signing up ' + this.name + ' email ' + this.email + ' password ' + this.password);

    let userSignUp = {
      name: this.name,
      email: this.email,
      password : this.password
    }

    this.taskService.postUserSignUp(userSignUp).subscribe(
      data => {
        console.log(data);
      },
      err => console.log('Could not reach heroku.'),
      () => console.log('Sign up complete.')
    );
  }
}
