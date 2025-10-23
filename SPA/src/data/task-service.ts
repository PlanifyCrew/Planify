import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
// import { of } from 'rxjs'; // nur solange dummy_token benutzt wird

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type' : 'application/json'
  })
}

@Injectable()
export class TaskService {

  constructor(private http: HttpClient) { }

  getTaskData(postTokenData: any) {
    //return of({ token: 'dummy_token_123' });
    return this.http.get('http://localhost:8090/api/task?token=' + postTokenData);
  //https://stormy-shore-22254-b36f0b7e9adf.herokuapp.com/api/task?token=
  }

  postUserLogIn(postUserLoginData: Object) {
    //return of({ token: 'dummy_token_123' });
    return this.http.post('http://localhost:8090/api/login', postUserLoginData);
 //https://stormy-shore-22254-b36f0b7e9adf.herokuapp.com/api/login
  }

  postUserSignUp(postUserSignUpData: Object) {
    return this.http.post('http://localhost:8090/api/user', postUserSignUpData);
  }

  postAddEvent(postAddEventData: Object) {
    return this.http.post('http://localhost:8090/api/event', postAddEventData);
  }

  getEventListData(eventListData: any): Observable<any> {
    const params = {
      token: eventListData.token,
      startDate: eventListData.startDate.toISOString().split('T')[0],
      endDate: eventListData.endDate.toISOString().split('T')[0]
    };

    return this.http.get('http://localhost:8090/api/event', { params });
  }

}
