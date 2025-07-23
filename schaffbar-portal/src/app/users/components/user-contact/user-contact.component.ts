import { Component, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { MatInputModule } from '@angular/material/input';

import { User } from '../../user.model';

@Component({
  selector: 'schbar-user-contact',
  templateUrl: './user-contact.component.html',
  styleUrl: './user-contact.component.scss',
  imports: [MatInputModule, ReactiveFormsModule],
})
export class UserContactComponent {
  user = input.required<User>();
}
