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
import { WorkshopSession } from '../../workshop-session.model';
import { WorkshopSessionService } from '../../workshop-session.service';

interface UserOpenSessionState {
  userId: string | null;
  openSession: WorkshopSession | null;
  dirty: boolean;
}

const initialState: UserOpenSessionState = {
  userId: null,
  openSession: null,
  dirty: false,
};

export const UserOpenSessionStore = signalStore(
  withState(initialState),
  withComputed(({ openSession }) => ({
    slots: computed(() => openSession()?.workshopUsages ?? []),
  })),
  withComputed(({ slots }) => ({
    slotsCount: computed(() => slots().length),
    totalTimeInMinutes: computed(() => slots().reduce((sum, usage) => sum + usage.durationInMinutes, 0)),
  })),
  withRequestStatus(),
  withProps(() => ({
    _workshopSessionService: inject(WorkshopSessionService),
  })),
  withMethods((store) => ({
    setUserId: signalMethod<string>((userId) => {
      patchState(store, { userId }, setDirty());
    }),
    reloadOpenSession: signalMethod<void>(() => {
      patchState(store, setDirty());
    }),
    loadOpenSession: rxMethod<string>(
      pipe(
        filter((userId: string) => !!userId),
        tap(() => patchState(store, setPending())),
        // delay(200), // TODO: Simulate network latency
        exhaustMap((userId: string) => {
          return store._workshopSessionService.getWorkshopSessionByUser(userId).pipe(
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
  })),
  withHooks({
    onInit(store) {
      effect(() => {
        const userId = store.userId();
        const dirty = store.dirty();
        if (dirty && userId) {
          console.log('[Store - onInit] Loading user open session for userId:', userId, 'Dirty:', dirty);
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
