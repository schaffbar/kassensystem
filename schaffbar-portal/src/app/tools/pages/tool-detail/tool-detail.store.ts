import { effect, inject } from '@angular/core';

import { tapResponse } from '@ngrx/operators';
import { patchState, signalMethod, signalStore, withHooks, withMethods, withProps, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { exhaustMap, filter, pipe, tap } from 'rxjs';

import { setError, setFulfilled, setPending, withRequestStatus } from '../../../shared/state/request-status.feature';
import {
  BatchCreateToolCertificationCommand,
  ToolCertification,
} from '../../../users/shared/models/tool-certification.model';
import { ToolCertificationService } from '../../../users/shared/services/tool-certification.service';
import { ChangeRfidReaderCommand, SetWlanRelaisCommand, Tool, UpdateToolCommand } from '../../tool.model';
import { ToolsService } from '../../tools.service';

interface ToolDetailState {
  toolId: string | null;
  tool: Tool | null;
  toolCertifications: ToolCertification[];
  dirty: boolean;
}

const initialState: ToolDetailState = {
  toolId: null,
  tool: null,
  toolCertifications: [],
  dirty: false,
};

export const ToolDetailStore = signalStore(
  withState(initialState),
  withRequestStatus(),
  withProps(() => ({
    _toolsService: inject(ToolsService),
    _toolCertificationService: inject(ToolCertificationService),
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
    clearRfidReader: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((toolId: string) => {
          return store._toolsService.clearRfidReader(toolId).pipe(
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
    setWlanRelais: rxMethod<SetWlanRelaisCommand>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((command: SetWlanRelaisCommand) => {
          return store._toolsService.setWlanRelais(command).pipe(
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
    clearWlanRelais: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((toolId: string) => {
          return store._toolsService.clearWlanRelais(toolId).pipe(
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
    addInstructors: rxMethod<{ toolId: string; instructorIds: string[] }>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap(({ toolId, instructorIds }) => {
          return store._toolsService.addInstructors(toolId, instructorIds).pipe(
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
    removeInstructors: rxMethod<{ toolId: string; instructorIds: string[] }>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap(({ toolId, instructorIds }) => {
          return store._toolsService.removeInstructors(toolId, instructorIds).pipe(
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
    loadToolCertifications: rxMethod<string>(
      pipe(
        filter((toolId: string) => !!toolId),
        tap(() => patchState(store, setPending())),
        exhaustMap((toolId: string) => {
          return store._toolCertificationService.getCertificationsByTool(toolId).pipe(
            tapResponse({
              next: (toolCertifications) => {
                patchState(store, { toolCertifications }, setFulfilled());
              },
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
    batchCreateCertifications: rxMethod<BatchCreateToolCertificationCommand>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((command: BatchCreateToolCertificationCommand) => {
          return store._toolCertificationService.batchCreate(command).pipe(
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
    deleteCertification: rxMethod<{ customerId: string; toolId: string }>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap(({ customerId, toolId }) => {
          return store._toolCertificationService.delete(customerId, toolId).pipe(
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
          store.loadToolCertifications(toolId);
        }
        patchState(store, { dirty: false });
      });
    },
  }),
);

export function setDirty(): { dirty: boolean } {
  return { dirty: true };
}
