import { Component, computed, ElementRef, input, output, signal, viewChild } from '@angular/core';

import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';

import { TranslatePipe } from '@ngx-translate/core';

import { User } from '../../../users/shared/models/user.model';

@Component({
  selector: 'schbar-tool-instructors',
  templateUrl: './tool-instructors.component.html',
  styleUrl: './tool-instructors.component.scss',
  imports: [
    MatAutocompleteModule,
    MatButtonModule,
    MatCheckboxModule,
    MatChipsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    TranslatePipe,
  ],
})
export class ToolInstructorsComponent {
  toolId = input.required<string>();
  instructorIds = input.required<string[]>();
  allUsers = input.required<User[]>();

  instructorsAdded = output<{ toolId: string; instructorIds: string[] }>();
  instructorsRemoved = output<{ toolId: string; instructorIds: string[] }>();

  searchText = signal('');
  selectedUsersToAdd = signal<User[]>([]);
  selectedInstructorIdsToRemove = signal<Set<string>>(new Set());

  private searchInput = viewChild<ElementRef>('searchInput');

  instructors = computed(() => {
    const ids = this.instructorIds();
    return this.allUsers().filter((user) => ids.includes(user.id));
  });

  filteredUsers = computed(() => {
    const search = this.searchText().toLowerCase();
    const currentIds = this.instructorIds();
    const selectedIds = this.selectedUsersToAdd().map((u) => u.id);

    return this.allUsers()
      .filter((user) => !currentIds.includes(user.id))
      .filter((user) => !selectedIds.includes(user.id))
      .filter(
        (user) =>
          !search ||
          user.firstName.toLowerCase().includes(search) ||
          user.lastName.toLowerCase().includes(search),
      );
  });

  selectUser(user: User): void {
    this.selectedUsersToAdd.update((users) => [...users, user]);
    this.searchText.set('');
    const input = this.searchInput();
    if (input) {
      input.nativeElement.value = '';
    }
  }

  removeSelectedUser(user: User): void {
    this.selectedUsersToAdd.update((users) => users.filter((u) => u.id !== user.id));
  }

  addInstructors(): void {
    const ids = this.selectedUsersToAdd().map((u) => u.id);
    if (ids.length > 0) {
      this.instructorsAdded.emit({ toolId: this.toolId(), instructorIds: ids });
      this.selectedUsersToAdd.set([]);
    }
  }

  toggleInstructorSelection(instructorId: string): void {
    this.selectedInstructorIdsToRemove.update((set) => {
      const newSet = new Set(set);
      if (newSet.has(instructorId)) {
        newSet.delete(instructorId);
      } else {
        newSet.add(instructorId);
      }
      return newSet;
    });
  }

  removeInstructors(): void {
    const ids = Array.from(this.selectedInstructorIdsToRemove());
    if (ids.length > 0) {
      this.instructorsRemoved.emit({ toolId: this.toolId(), instructorIds: ids });
      this.selectedInstructorIdsToRemove.set(new Set());
    }
  }
}
