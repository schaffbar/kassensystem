import { Routes } from '@angular/router';

import { AboutComponent } from './core/about/about.component';
import { HomeComponent } from './core/home/home.component';
import { NotFoundComponent } from './core/not-found/not-found.component';
import { UserDetailComponent } from './users/pages/user-detail/user-detail.component';
import { UserListComponent } from './users/pages/user-list/user-list.component';

export const ROUTE_TOKENS = {
  HOME: 'home',
  ABOUT: 'about',
  USERS: 'users',
  PRODUCTS: 'products',
};

export const routes: Routes = [
  {
    path: ROUTE_TOKENS.HOME,
    component: HomeComponent,
  },
  {
    path: ROUTE_TOKENS.USERS,
    component: UserListComponent,
  },
  {
    path: `${ROUTE_TOKENS.USERS}/:id`,
    component: UserDetailComponent,
  },
  {
    path: ROUTE_TOKENS.ABOUT,
    component: AboutComponent,
  },
  {
    path: '',
    redirectTo: ROUTE_TOKENS.HOME,
    pathMatch: 'full',
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];
