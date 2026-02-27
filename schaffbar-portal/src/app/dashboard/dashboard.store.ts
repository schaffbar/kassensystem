import { inject } from '@angular/core';

import { tapResponse } from '@ngrx/operators';
import { patchState, signalStore, withHooks, withMethods, withProps } from '@ngrx/signals';
import { SelectEntityId, setAllEntities, withEntities } from '@ngrx/signals/entities';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { exhaustMap, pipe, tap } from 'rxjs';

import { setError, setFulfilled, setPending, withRequestStatus } from '../shared/state/request-status.feature';
import { ActiveUser } from './dashboard.model';
import { DashboardService } from './dashboard.service';

const selectId: SelectEntityId<ActiveUser> = (user) => user.customerId;

export const DashboardStore = signalStore(
  withEntities<ActiveUser>(),
  withRequestStatus(),
  withProps(() => ({
    _dashboardService: inject(DashboardService),
  })),
  withMethods((store) => ({
    loadActiveUsers: rxMethod<void>(
      pipe(
        tap(() => patchState(store, setPending())),
        exhaustMap(() => {
          return store._dashboardService.getActiveUsers().pipe(
            tapResponse({
              next: (activeUsers) => patchState(store, setAllEntities(activeUsers, { selectId }), setFulfilled()),
              error: (error: { message: string }) => patchState(store, setError(error.message)),
            }),
          );
        }),
      ),
    ),
  })),
  withHooks((store) => ({
    onInit() {
      console.log('[DashboardStore - onInit] Loading active users');
      store.loadActiveUsers();
    },
    onDestroy() {
      console.log('[DashboardStore - onDestroy] Store destroyed');
    },
  })),
);
