import {inject} from "@angular/core";
import {HttpEvent, HttpHandlerFn, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthService} from "@app/_services";
import {environment} from "@environments/environment";

export function httpInterceptor(request: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> {
  const auth: AuthService = inject(AuthService);
  return intercept();

  function intercept() {
    if (auth.userValue == null || !request.url.startsWith(environment.apiUrl)) {
      return next(request);
    }
    const user = auth.userValue;
    request = request.clone({
      withCredentials: true,
      setHeaders: {
        'Authorization': `Basic ${btoa(user?.username + ":" + user?.password)}`
      }
    })
    return next(request);
  }
}
