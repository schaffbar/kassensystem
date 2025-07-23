import { inject } from '@angular/core';

import { tapResponse } from '@ngrx/operators';
import { patchState, signalStore, withHooks, withMethods, withProps } from '@ngrx/signals';
import { setAllEntities, setEntity, withEntities } from '@ngrx/signals/entities';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { delay, exhaustMap, pipe, tap } from 'rxjs';

import { setError, setFulfilled, setPending, withRequestStatus } from '../shared/state/request-status.feature';
import { User } from './user.model';
import { UsersService } from './users.service';

export const UsersStore = signalStore(
  { providedIn: 'root' },
  withEntities<User>(),
  withRequestStatus(),
  withProps(() => ({
    _usersService: inject(UsersService),
  })),
  withMethods((store) => ({
    setUser(user: User): void {
      patchState(store, setEntity(user));
    },
    loadAllUsers: rxMethod<void>(
      pipe(
        tap(() => patchState(store, setPending())),
        delay(100), // TODO: Simulate network latency
        exhaustMap(() => {
          return store._usersService.getUsers().pipe(
            delay(400),
            tapResponse({
              next: (users) => patchState(store, setAllEntities(users), setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
  })),
  withMethods((store) => ({
    createUser: rxMethod<User>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((user: User) => {
          return store._usersService.createUser(user).pipe(
            tapResponse({
              next: () => patchState(store, setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
            tap(() => store.loadAllUsers()),
          );
        }),
      ),
    ),
    deleteUser: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((userId: string) => {
          return store._usersService.deleteUser(userId).pipe(
            tapResponse({
              next: () => patchState(store, setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
            tap(() => store.loadAllUsers()),
          );
        }),
      ),
    ),
  })),
  withHooks((store) => ({
    onInit() {
      console.log('[Store - onInit] Loading all users');
      store.loadAllUsers();
    },
    onDestroy() {
      console.log('[Store - onDestroy] Store destroyed, resetting state');
      // patchState(store, {
      //   isLoading: false,
      //   currentPage: 1,
      //   pageCount: 1,
      // });
      // patchState(store, removeAllEntities({ collection: 'task' }));
    },
  })),
);
