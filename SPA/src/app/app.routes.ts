
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'auth',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('./home/home').then(m => m.HomeComponent)
  },
  {
    path: 'auth',
    loadComponent: () => import('./auth/auth').then(m => m.Auth)
  },
  {
    path: 'add-event',
    loadComponent: () => import('./home/Add-Event/Add-Event').then(m => m.AddEvent)
  },
  { path: '**', redirectTo: 'auth', pathMatch: 'full' }
];

