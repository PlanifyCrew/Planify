import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import gql from 'graphql-tag';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class GraphqlService {
  constructor(private apollo: Apollo) {}

    login(email: string, password: string): Observable<any> {
        return this.apollo.mutate({
            mutation: gql`
                mutation($email: String!, $password: String!) {
                    login(email: $email, password: $password) {
                        token
                        status
                    }
                }
            `,
            variables: { email, password }
        });
    }

    logout(token: string): Observable<any> {
        return this.apollo.mutate({
            mutation: gql`
                mutation($token: String!) {
                    logout(token: $token) {
                        message
                    }
                }
            `,
            variables: { token }
        });
    }

    createUser(name: string, email: string, password: string): Observable<any> {
        return this.apollo.mutate({
            mutation: gql`
                mutation($name: String!, $email: String!, $password: String!) {
                    createUser(name: $name, email: $email, password: $password) {
                        message
                    }
                }
            `,
            variables: { name, email, password }
        });
    }

    addEvent(token: string, event: any, tnListe: string[]): Observable<any> {
        return this.apollo.mutate({
            mutation: gql`
                mutation($token: String!, $event: EventInput!, $tnListe: [String]!) {
                    addEvent(token: $token, event: $event, tnListe: $tnListe) {
                        message
                    }
                }
            `,
            variables: { token, event, tnListe }
        });
    }
  
}
