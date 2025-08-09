import { inject } from '@angular/core';

import { tapResponse } from '@ngrx/operators';
import { patchState, signalStore, withHooks, withMethods, withProps } from '@ngrx/signals';
import { setAllEntities, withEntities } from '@ngrx/signals/entities';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { delay, exhaustMap, pipe, tap } from 'rxjs';

import { setError, setFulfilled, setPending, withRequestStatus } from '../shared/state/request-status.feature';
import { RfidTag } from './rfid-tag.model';
import { RfidTagService } from './rfid-tag.service';

export const RfidTagStore = signalStore(
  { providedIn: 'root' },
  withEntities<RfidTag>(),
  withRequestStatus(),
  withProps(() => ({
    _rfidTagService: inject(RfidTagService),
  })),
  withMethods((store) => ({
    loadAllRfidTags: rxMethod<void>(
      pipe(
        tap(() => patchState(store, setPending())),
        delay(100), // TODO: Simulate network latency
        exhaustMap(() => {
          return store._rfidTagService.getRfidTags().pipe(
            delay(400),
            tapResponse({
              next: (rfidTags) => patchState(store, setAllEntities(rfidTags), setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
  })),
  withMethods((store) => ({
    deleteRfidTag: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((rfidTagId: string) => {
          return store._rfidTagService.deleteRfidTag(rfidTagId).pipe(
            tapResponse({
              next: () => patchState(store, setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
            tap(() => store.loadAllRfidTags()),
          );
        }),
      ),
    ),
  })),
  withHooks((store) => ({
    onInit() {
      console.log('[Store - onInit] Loading all RFID tags');
      store.loadAllRfidTags();
    },
    onDestroy() {
      console.log('[Store - onDestroy] Store destroyed, resetting state');
    },
  })),
);
