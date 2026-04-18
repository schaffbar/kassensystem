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
import { ChangeRfidReaderTypeCommand, RfidReader, UpdateRfidReaderCommand } from '../../rfid-reader.model';
import { RfidReaderService } from '../../rfid-readers.service';

interface RfidReaderDetailState {
  rfidReaderId: string | null;
  rfidReader: RfidReader | null;
  dirty: boolean;
}

const initialState: RfidReaderDetailState = {
  rfidReaderId: null,
  rfidReader: null,
  dirty: false,
};

function setDirty() {
  return { dirty: true };
}

export const RfidReaderDetailStore = signalStore(
  withState(initialState),
  withComputed(({ rfidReader }) => ({
    readerName: computed(() => (rfidReader() ? rfidReader()?.name || rfidReader()?.macAddress : 'N/A')),
  })),
  withRequestStatus(),
  withProps(() => ({
    _rfidReaderService: inject(RfidReaderService),
  })),
  withMethods((store) => ({
    setRfidReaderId: signalMethod<string>((rfidReaderId) => {
      patchState(store, { rfidReaderId }, setDirty());
    }),
    reloadRfidReader: signalMethod<void>(() => {
      patchState(store, setDirty());
    }),
    loadSelectedRfidReader: rxMethod<string>(
      pipe(
        filter((rfidReaderId: string) => !!rfidReaderId),
        tap(() => patchState(store, setPending())),
        exhaustMap((rfidReaderId: string) => {
          return store._rfidReaderService.getRfidReader(rfidReaderId).pipe(
            tapResponse({
              next: (rfidReader) => {
                patchState(store, { rfidReader }, setFulfilled());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
    updateRfidReader: rxMethod<UpdateRfidReaderCommand>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((command) => {
          return store._rfidReaderService.updateRfidReader(command).pipe(
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
    changeRfidReaderType: rxMethod<ChangeRfidReaderTypeCommand>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((command) => {
          return store._rfidReaderService.changeRfidReaderType(command).pipe(
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
      effect(() => {
        const rfidReaderId = store.rfidReaderId();
        const dirty = store.dirty();
        if (dirty && rfidReaderId) {
          store.loadSelectedRfidReader(rfidReaderId);
          patchState(store, { dirty: false });
        }
      });
    },
  }),
);
