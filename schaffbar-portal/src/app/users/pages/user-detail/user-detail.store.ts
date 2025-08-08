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
import { RfidTagAssignment, RfidTagAssignmentStatus } from '../../rfid-tag-assignment.model';
import { RfidTagAssignmentService } from '../../rfid-tag-assignment.service';
import { User } from '../../user.model';
import { UsersService } from '../../users.service';

interface UserDetailState {
  userId: string | null;
  user: User | null;
  rfidTagAssignment: RfidTagAssignment | null;
  dirty: boolean;
}

const initialState: UserDetailState = {
  userId: null,
  user: null,
  rfidTagAssignment: null,
  dirty: false,
};

export const UserDetailStore = signalStore(
  withState(initialState),
  withComputed(({ user, rfidTagAssignment }) => ({
    userName: computed(() => (user() ? user()?.firstName + ' ' + user()?.lastName : 'N/A')),
    isRfidTagAssigned: computed(
      () => !!rfidTagAssignment() && rfidTagAssignment()?.status == RfidTagAssignmentStatus.Assigned,
    ),
    waitingForAssignment: computed(
      () => !!rfidTagAssignment() && rfidTagAssignment()?.status == RfidTagAssignmentStatus.WaitingForAssignment,
    ),
    showAssignActions: computed(
      () => !rfidTagAssignment() || rfidTagAssignment()?.status == RfidTagAssignmentStatus.Unassigned,
    ),
  })),
  withRequestStatus(),
  withProps(() => ({
    _usersService: inject(UsersService),
    _assignmentService: inject(RfidTagAssignmentService),
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
    loadRfidTagAssignments: rxMethod<string>(
      pipe(
        filter((userId: string) => !!userId),
        tap(() => patchState(store, setPending())),
        delay(100), // TODO: Simulate network latency
        exhaustMap((userId: string) => {
          return store._assignmentService.getRfidTagAssignmetByUser(userId).pipe(
            tapResponse({
              next: (rfidTagAssignment) => {
                patchState(store, { rfidTagAssignment }, setFulfilled());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
    assignFixedRfidTag: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((userId: string) => {
          return store._assignmentService.assignFixedRfidTag(userId).pipe(
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
    assignTemporaryRfidTag: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((userId: string) => {
          return store._assignmentService.assignTemporaryRfidTag(userId).pipe(
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
            store.loadRfidTagAssignments(userId);
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
