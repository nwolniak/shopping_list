import {bootstrapApplication} from "@angular/platform-browser";
import {AppComponent} from "@app/app.component";
import {provideRouter} from "@angular/router";
import {APP_ROUTES} from "@app/app.routes";
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {httpInterceptor} from "@app/_helpers";
import {provideAnimations} from "@angular/platform-browser/animations";


bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(APP_ROUTES),
    provideHttpClient(
      withInterceptors([
        httpInterceptor,
      ])
    ),
    provideAnimations(),
  ]
})
