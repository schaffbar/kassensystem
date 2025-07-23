import { Component } from '@angular/core';

import { NavigationComponent } from './core/navigation/navigation.component';

@Component({
  selector: 'schbar-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  imports: [NavigationComponent],
})
export class AppComponent {
  title = 'schaffbar-portal';
}
