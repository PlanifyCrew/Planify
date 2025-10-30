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

    updateEvent(event_id: number, token: string, event: any, tnListe: string[]): Observable<any> {
        return this.apollo.mutate({
            mutation: gql`
                mutation($event_id: ID!, $token: String!, $event: EventInput!, $tnListe: [String]!) {
                    updateEvent(event_id: $event_id, token: $token, event: $event, tnListe: $tnListe) {
                        message
                    }
                }
            `,
            variables: { event_id, token, tnListe }
        });
    }
    
    getEvents(token: string, startDate: string, endDate: string): Observable<any> {
        return this.apollo.watchQuery({
        query: gql`
            query($token: String!, $startDate: String, $endDate: String) {
                events(token: $token, startDate: $startDate, endDate: $endDate) {
                    event_id
                    name
                    date
                    description
                    start_time
                    end_time
                }
            }
        `,
        variables: { token, startDate, endDate },
        fetchPolicy: 'network-only', // optional: um Cache zu umgehen
        }).valueChanges;
    }

    deleteEvent(event_id: number, token: string): Observable<any> {
        return this.apollo.mutate({
            mutation: gql`
                mutation($event_id: ID!, $token: String!) {
                    deleteEvent(eventId: $event_id, token: $token) {
                        message
                    }
                }
            `,
            variables: { event_id, token }
        });
    }
  
}
