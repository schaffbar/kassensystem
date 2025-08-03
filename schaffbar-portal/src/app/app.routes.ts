import { Routes } from '@angular/router';

import { AboutComponent } from './core/about/about.component';
import { HomeComponent } from './core/home/home.component';
import { NotFoundComponent } from './core/not-found/not-found.component';
import { ToolDetailComponent } from './tools/pages/tool-detail/tool-detail.component';
import { ToolListComponent } from './tools/pages/tool-list/tool-list.component';
import { UserDetailComponent } from './users/pages/user-detail/user-detail.component';
import { UserListComponent } from './users/pages/user-list/user-list.component';

export const ROUTE_TOKENS = {
  HOME: 'home',
  ABOUT: 'about',
  USERS: 'users',
  TOOLS: 'tools',
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
    path: ROUTE_TOKENS.TOOLS,
    component: ToolListComponent,
  },
  {
    path: `${ROUTE_TOKENS.TOOLS}/:id`,
    component: ToolDetailComponent,
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
