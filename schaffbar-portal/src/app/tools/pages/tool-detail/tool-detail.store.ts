import { effect, inject } from '@angular/core';

import { tapResponse } from '@ngrx/operators';
import { patchState, signalMethod, signalStore, withHooks, withMethods, withProps, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { exhaustMap, filter, pipe, tap } from 'rxjs';

import { setError, setFulfilled, setPending, withRequestStatus } from '../../../shared/state/request-status.feature';
import { UpdateToolCommand } from '../../components/tool-detail-info/tool-detail-info.component';
import { ChangeRfidReaderCommand } from '../../components/tool-rfid-reader-assignment/tool-rfid-reader-assignment.component';
import { Tool } from '../../tool.model';
import { ToolsService } from '../../tools.service';

interface ToolDetailState {
  toolId: string | null;
  tool: Tool | null;
  dirty: boolean;
}

const initialState: ToolDetailState = {
  toolId: null,
  tool: null,
  dirty: false,
};

export const ToolDetailStore = signalStore(
  withState(initialState),
  withRequestStatus(),
  withProps(() => ({
    _toolsService: inject(ToolsService),
  })),
  withMethods((store) => ({
    setToolId: signalMethod<string>((toolId) => {
      patchState(store, { toolId }, setDirty());
    }),
    reloadTool: signalMethod<void>(() => {
      patchState(store, setDirty());
    }),
    loadSelectedTool: rxMethod<string>(
      pipe(
        filter((toolId: string) => !!toolId),
        tap(() => patchState(store, setPending())),
        // delay(200), // TODO: Simulate network latency
        exhaustMap((toolId: string) => {
          return store._toolsService.getTool(toolId).pipe(
            tapResponse({
              next: (tool) => {
                patchState(store, { tool }, setFulfilled());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
    updateTool: rxMethod<UpdateToolCommand>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((command: UpdateToolCommand) => {
          return store._toolsService.updateTool(command).pipe(
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
    changeRfidReader: rxMethod<ChangeRfidReaderCommand>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((command: ChangeRfidReaderCommand) => {
          return store._toolsService.changeRfidReader(command).pipe(
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
        const toolId = store.toolId();
        const dirty = store.dirty();
        if (dirty && toolId) {
          console.log('[Store - onInit] Loading tool details for toolId:', toolId, 'Dirty:', dirty);
          store.loadSelectedTool(toolId);
        }
        patchState(store, { dirty: false });
      });
    },
  }),
);

export function setDirty(): { dirty: boolean } {
  return { dirty: true };
}
