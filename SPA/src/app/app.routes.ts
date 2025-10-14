import { Routes } from '@angular/router';

export const routes: Routes = [
	{
		path: '',
		loadComponent: () => import('./home/home').then(m => m.Home)
	},
	{
		path: 'auth',
		loadComponent: () => import('./auth/auth').then(m => m.Auth)
	},
	// fallback to home
	{ path: '**', redirectTo: '', pathMatch: 'full' }
];
