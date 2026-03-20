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
import { exhaustMap, filter, pipe, tap } from 'rxjs';

import { setError, setFulfilled, setPending, withRequestStatus } from '../../../shared/state/request-status.feature';
import { RfidTagAssignment, RfidTagAssignmentStatus } from '../../shared/models/rfid-tag-assignment.model';
import { User, UserAddress } from '../../shared/models/user.model';
import { WorkshopSession } from '../../shared/models/workshop-session.model';
import { RfidTagAssignmentService } from '../../shared/services/rfid-tag-assignment.service';
import { UsersService } from '../../shared/services/users.service';
import { WorkshopSessionService } from '../../shared/services/workshop-session.service';

interface UserDetailState {
  userId: string | null;
  user: User | null;
  rfidTagAssignment: RfidTagAssignment | null;
  openSession: WorkshopSession | null;
  dirty: boolean;
}

const initialState: UserDetailState = {
  userId: null,
  user: null,
  rfidTagAssignment: null,
  openSession: null,
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
    showUnassignAction: computed(
      () => !!rfidTagAssignment() && rfidTagAssignment()?.status == RfidTagAssignmentStatus.Assigned,
    ),
  })),
  withComputed(({ openSession }) => ({
    slots: computed(() => openSession()?.workshopUsages ?? []),
    toolUsageSummaries: computed(() => openSession()?.toolUsageSummaries ?? []),
  })),
  withComputed(({ slots }) => ({
    slotsCount: computed(() => slots().length),
    totalTimeInMinutes: computed(() => slots().reduce((sum, usage) => sum + usage.durationInMinutes, 0)),
    totalUnitsUsed: computed(() => slots().reduce((sum, usage) => sum + usage.unitsUsed, 0)),
  })),
  withRequestStatus(),
  withProps(() => ({
    _usersService: inject(UsersService),
    _assignmentService: inject(RfidTagAssignmentService),
    _workshopSessionService: inject(WorkshopSessionService),
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
        // delay(200), // TODO: Simulate network latency
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
    updateUser: rxMethod<{ id: string; firstName: string; lastName: string; dateOfBirth: Date; clubMember: boolean }>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap(({ id, firstName, lastName, dateOfBirth, clubMember }) => {
          return store._usersService.updateUser(id, { firstName, lastName, dateOfBirth, clubMember }).pipe(
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
    updateUserContact: rxMethod<{ id: string; email: string; phone: string }>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap(({ id, email, phone }) => {
          return store._usersService.updateUserContact(id, { email, phone }).pipe(
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
    updateUserAddress: rxMethod<{ id: string; address: UserAddress }>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap(({ id, address }) => {
          return store._usersService.updateUserAddress(id, address).pipe(
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
  withMethods((store) => ({
    loadRfidTagAssignments: rxMethod<string>(
      pipe(
        filter((userId: string) => !!userId),
        tap(() => patchState(store, setPending())),
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
    unassignRfidTag: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((userId: string) => {
          return store._assignmentService.unassignRfidTag(userId).pipe(
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
  withMethods((store) => ({
    loadOpenSession: rxMethod<string>(
      pipe(
        filter((userId: string) => !!userId),
        tap(() => patchState(store, setPending())),
        exhaustMap((userId: string) => {
          return store._workshopSessionService.getActiveWorkshopSession(userId).pipe(
            tapResponse({
              next: (openSession) => {
                patchState(store, { openSession }, setFulfilled());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
    closeSession: rxMethod<string>(
      pipe(
        filter((userId: string) => !!userId),
        tap(() => patchState(store, setPending())),
        exhaustMap((userId: string) => {
          return store._workshopSessionService.closeWorkshopSession(userId).pipe(
            tapResponse({
              next: () => {
                patchState(store, setDirty(), setFulfilled());
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
      effect(() => {
        const userId = store.userId();
        const dirty = store.dirty();
        if (dirty && userId) {
          console.log('[Store - onInit] Loading user details for userId:', userId, ', dirty:', dirty);
          store.loadSelectedUser(userId);
          store.loadRfidTagAssignments(userId);
          store.loadOpenSession(userId);
          patchState(store, { dirty: false });
        }
      });
    },
  }),
);

export function setDirty(): { dirty: boolean } {
  return { dirty: true };
}
