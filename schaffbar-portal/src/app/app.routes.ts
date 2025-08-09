import { Routes } from '@angular/router';

import { AboutComponent } from './core/about/about.component';
import { HomeComponent } from './core/home/home.component';
import { NotFoundComponent } from './core/not-found/not-found.component';
import { RfidTagListComponent } from './rfid-tags/pages/rfid-tag-list/rfid-tag-list.component';
import { ToolDetailComponent } from './tools/pages/tool-detail/tool-detail.component';
import { ToolListComponent } from './tools/pages/tool-list/tool-list.component';
import { UserDetailComponent } from './users/pages/user-detail/user-detail.component';
import { UserListComponent } from './users/pages/user-list/user-list.component';

export const ROUTE = {
  HOME: 'home',
  ABOUT: 'about',
  USERS: 'users',
  TOOLS: 'tools',
  RFID_TAGS: 'rfid-tags',
};

export const routes: Routes = [
  {
    path: ROUTE.HOME,
    component: HomeComponent,
  },
  {
    path: ROUTE.USERS,
    component: UserListComponent,
  },
  {
    path: `${ROUTE.USERS}/:id`,
    component: UserDetailComponent,
  },
  {
    path: ROUTE.TOOLS,
    component: ToolListComponent,
  },
  {
    path: `${ROUTE.TOOLS}/:id`,
    component: ToolDetailComponent,
  },
  {
    path: ROUTE.RFID_TAGS,
    component: RfidTagListComponent,
  },
  {
    path: ROUTE.ABOUT,
    component: AboutComponent,
  },
  {
    path: '',
    redirectTo: ROUTE.HOME,
    pathMatch: 'full',
  },
  {
    path: '**',
    component: NotFoundComponent,
  },
];
