import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';

import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

export interface MenuItem {
  icon: string;
  name: string;
  route: string;
}

@Component({
  selector: 'schbar-navigation',
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    AsyncPipe,
  ],
})
export class NavigationComponent {
  menuItems = signal<MenuItem[]>([
    { icon: 'home', name: 'Home', route: 'home' },
    { icon: 'people', name: 'Users', route: 'users' },
    // { icon: 'construction', name: 'Products', route: 'products' },
    { icon: 'info', name: 'About', route: 'about' },
  ]);

  private breakpointObserver = inject(BreakpointObserver);

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map((result) => result.matches),
    shareReplay(),
  );
}
