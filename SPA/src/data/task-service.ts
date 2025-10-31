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
    return this.http.get('https://bff-planify-slay-78df7e83cb88.herokuapp.com/api/task?token=' + postTokenData);
  //https://stormy-shore-22254-b36f0b7e9adf.herokuapp.com/api/task?token=
  }

  postUserLogIn(postUserLoginData: Object) {
    //return of({ token: 'dummy_token_123' });
    return this.http.post('https://bff-planify-slay-78df7e83cb88.herokuapp.com/api/login', postUserLoginData);
 //https://stormy-shore-22254-b36f0b7e9adf.herokuapp.com/api/login
  }

  postUserLogOut(postUserLogOutData: Object) {
    return this.http.post('https://bff-planify-slay-78df7e83cb88.herokuapp.com/api/logout', postUserLogOutData);
  }

  postUserSignUp(postUserSignUpData: Object) {
    return this.http.post('https://bff-planify-slay-78df7e83cb88.herokuapp.com/api/user', postUserSignUpData);
  }

  postAddEvent(postAddEventData: Object) {
    return this.http.post('https://bff-planify-slay-78df7e83cb88.herokuapp.com/api/event', postAddEventData);
  }

  getEventListData(eventListData: any): Observable<any> {
    const params = {
      token: eventListData.token,
      startDate: eventListData.startDate.toISOString().split('T')[0],
      endDate: eventListData.endDate.toISOString().split('T')[0]
    };

    return this.http.get('https://bff-planify-slay-78df7e83cb88.herokuapp.com/api/event', { params });
  }

  postAddUser(postAddUserData: Object) {
    //return of({ token: 'dummy_token_123' });
    return this.http.post('https://bff-planify-slay-78df7e83cb88.herokuapp.com/api/sendEmail', postAddUserData);
    //https://stormy-shore-22254-b36f0b7e9adf.herokuapp.com/api/addevent
  }

}
