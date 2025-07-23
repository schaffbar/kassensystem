import { Component, computed, inject, ViewChild } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { ROUTE_TOKENS } from '../../../app.routes';
import { NewUserFormComponent } from '../../components/new-user-form/new-user-form.component';
import { User } from '../../user.model';
import { UsersStore } from '../../users.store';

@Component({
  selector: 'schbar-user-list',
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss',
  standalone: true,
  imports: [
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  providers: [UsersStore],
})
export class UserListComponent {
  readonly store = inject(UsersStore);
  readonly router = inject(Router);
  readonly dialog = inject(MatDialog);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  protected displayedColumns = ['lastName', 'firstName', 'actions'];
  protected usersCount = computed(() => this.store.entities().length);
  protected dataSource = computed(() => {
    const result = new MatTableDataSource<User>(this.store.entities());
    result.sort = this.sort;
    result.paginator = this.paginator;
    return result;
  });

  protected applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource().filter = filterValue.trim().toLowerCase();

    if (this.dataSource().paginator) {
      this.dataSource().paginator?.firstPage();
    }
  }

  protected userDetails(user: User): void {
    this.router.navigate([ROUTE_TOKENS.USERS, user.id]);
  }

  protected newUserDialog(): void {
    const dialogRef = this.dialog.open(NewUserFormComponent, {
      minWidth: '1000px',
      disableClose: true,
      autoFocus: true,
    });

    dialogRef
      .afterClosed() //
      .subscribe((user: User) => {
        if (user) {
          this.store.createUser(user);
        }
      });
  }

  protected deleteUserDialog(event: Event, user: User): void {
    event.stopPropagation();

    if (!user || !user.id) {
      console.warn('No user selected for deletion');
      return;
    }

    this.store.deleteUser(user.id);
  }
}
