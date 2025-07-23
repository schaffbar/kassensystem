import { computed, effect, inject } from '@angular/core';

import { tapResponse } from '@ngrx/operators';
import {
  patchState,
  signalMethod,
  signalStore,
  withComputed,
  withHooks,
  withMethods,
  withProps,
  withState,
} from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { delay, exhaustMap, filter, pipe, tap } from 'rxjs';

import { setError, setFulfilled, setPending, withRequestStatus } from '../../../shared/state/request-status.feature';
import { TokenAssignment, TokenAssignmentStatus } from '../../token.model';
import { TokenService } from '../../token.service';
import { User } from '../../user.model';
import { UsersService } from '../../users.service';

interface UserDetailState {
  userId: string | null;
  user: User | null;
  tokenAssignment: TokenAssignment | null;
  dirty: boolean;
}

const initialState: UserDetailState = {
  userId: null,
  user: null,
  tokenAssignment: null,
  dirty: false,
};

export const UserDetailStore = signalStore(
  withState(initialState),
  withComputed(({ user, tokenAssignment }) => ({
    userName: computed(() => (user() ? user()?.firstName + ' ' + user()?.lastName : 'N/A')),
    isTokenAssigned: computed(() => !!tokenAssignment() && tokenAssignment()?.status == TokenAssignmentStatus.Assigned),
    waitingForAssignment: computed(
      () => !!tokenAssignment() && tokenAssignment()?.status == TokenAssignmentStatus.WaitingForAssignment,
    ),
    showAssignActions: computed(
      () => !tokenAssignment() || tokenAssignment()?.status == TokenAssignmentStatus.Unassigned,
    ),
  })),
  withRequestStatus(),
  withProps(() => ({
    _usersService: inject(UsersService),
    _tokenService: inject(TokenService),
  })),
  withMethods((store) => ({
    setUserId: signalMethod<string>((userId) => {
      patchState(store, { userId }, setDirty());
    }),
    reloadUser: signalMethod<void>(() => {
      patchState(store, setDirty());
    }),
    loadSelectedUser: rxMethod<string>(
      pipe(
        filter((userId: string) => !!userId),
        tap(() => patchState(store, setPending())),
        delay(200), // TODO: Simulate network latency
        exhaustMap((userId: string) => {
          return store._usersService.getUser(userId).pipe(
            tapResponse({
              next: (user) => {
                patchState(store, { user }, setFulfilled());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
    loadTokenAssignments: rxMethod<string>(
      pipe(
        filter((userId: string) => !!userId),
        tap(() => patchState(store, setPending())),
        delay(100), // TODO: Simulate network latency
        exhaustMap((userId: string) => {
          return store._tokenService.getTokenAssignmetByUser(userId).pipe(
            tapResponse({
              next: (tokenAssignment) => {
                patchState(store, { tokenAssignment }, setFulfilled());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
    assignToken: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((userId: string) => {
          return store._tokenService.assignToken(userId).pipe(
            tapResponse({
              next: () => {
                patchState(store, setFulfilled(), setDirty());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
    assignTemporaryToken: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((userId: string) => {
          return store._tokenService.assignTemporaryToken(userId).pipe(
            tapResponse({
              next: () => {
                patchState(store, setFulfilled(), setDirty());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
  })),
  withHooks({
    onInit(store) {
      effect(
        () => {
          const userId = store.userId();
          const dirty = store.dirty();
          if (dirty && userId) {
            console.log('[Store - onInit] Loading user details for userId:', userId, 'Dirty:', dirty);
            store.loadSelectedUser(userId);
            store.loadTokenAssignments(userId);
            patchState(store, { dirty: false });
          }
        },
        // TODO: do we neet it?
        { allowSignalWrites: true },
      );
    },
  }),
);

export function setDirty(): { dirty: boolean } {
  return { dirty: true };
}
