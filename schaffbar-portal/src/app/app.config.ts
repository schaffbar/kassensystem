import { DatePipe, registerLocaleData } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import localeDe from '@angular/common/locales/de';
import { ApplicationConfig, LOCALE_ID, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';

import { routes } from './app.routes';

registerLocaleData(localeDe);

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(),
    DatePipe,
    { provide: LOCALE_ID, useValue: 'de-DE' },
  ],
};
