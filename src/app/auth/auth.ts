import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TaskService } from '../../data/task-service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [FormsModule, HttpClientModule, CommonModule],
  templateUrl: './auth.html',
  styleUrls: ['./auth.css'],
  providers: [TaskService]
})

export class Auth implements OnInit {
  name: string = '';
  email: string = '';
  password: string = '';
  private token: any;

  constructor(private router: Router, private taskService: TaskService) { }
  ngOnInit() { }

  loginError: string | null = null;
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

        if (this.token && (this.token.token != 'OFF')) {
          localStorage.setItem('auth_token', this.token.token);
          this.loginError = null; // Erfolgreich → keine Fehlermeldung
          this.router.navigate(['/home']); // Navigation zur Home-Seite
        } else {
          this.loginError = 'Fehler! Falsche Email oder Passwort!'; // Fehlermeldung setzen
        }
      },
      err => {
        console.log('Could not reach heroku.'),
        this.loginError = 'Fehler! Falsche Email oder Passwort!'; // Fehlermeldung setzen
      },
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


  changeToSignup() {
    const x = document.getElementById("login");
    const y = document.getElementById("signup");
    const z = document.getElementById("over-login");
    const a = document.getElementById("over-signup");

    if (x && y && z && a) {
      x.style.left = "-400px";
      y.style.left = "420px";
      z.style.left = "900px";
      a.style.left = "0px";
    }
  }

  changeToLogin() {
    const x = document.getElementById("login");
    const y = document.getElementById("signup");
    const z = document.getElementById("over-login");
    const a = document.getElementById("over-signup");

    if (x && y && z && a) {
      x.style.left = "0px";
      y.style.left = "900px";
      z.style.left = "420px";
      a.style.left = "-900px";
    }
  }
}
