import { inject } from '@angular/core';

import { tapResponse } from '@ngrx/operators';
import { patchState, signalStore, withHooks, withMethods, withProps } from '@ngrx/signals';
import { setAllEntities, withEntities } from '@ngrx/signals/entities';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { delay, exhaustMap, pipe, tap } from 'rxjs';

import { setError, setFulfilled, setPending, withRequestStatus } from '../shared/state/request-status.feature';
import { Tool } from './tool.model';
import { ToolsService } from './tools.service';

export const ToolsStore = signalStore(
  { providedIn: 'root' },
  withEntities<Tool>(),
  withRequestStatus(),
  withProps(() => ({
    _toolsService: inject(ToolsService),
  })),
  withMethods((store) => ({
    loadAllTools: rxMethod<void>(
      pipe(
        tap(() => patchState(store, setPending())),
        delay(100), // TODO: Simulate network latency
        exhaustMap(() => {
          return store._toolsService.getTools().pipe(
            delay(400),
            tapResponse({
              next: (tools) => patchState(store, setAllEntities(tools), setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
  })),
  withMethods((store) => ({
    createTool: rxMethod<Tool>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((tool: Tool) => {
          return store._toolsService.createTool(tool).pipe(
            tapResponse({
              next: () => patchState(store, setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
            tap(() => store.loadAllTools()),
          );
        }),
      ),
    ),
    deleteTool: rxMethod<string>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap((toolId: string) => {
          return store._toolsService.deleteTool(toolId).pipe(
            tapResponse({
              next: () => patchState(store, setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
            tap(() => store.loadAllTools()),
          );
        }),
      ),
    ),
  })),
  withHooks((store) => ({
    onInit() {
      console.log('[Store - onInit] Loading all tools');
      store.loadAllTools();
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
