import {AuthService} from "@app/_services";
import {BehaviorSubject, of} from "rxjs";
import {TestBed} from "@angular/core/testing";
import {User} from "@app/_models";
import {HttpEventType, HttpRequest} from "@angular/common/http";
import {environment} from "@environments/environment";
import {httpInterceptor} from "@app/_helpers/http.interceptor";

describe("Http Interceptor", () => {
        let authService: AuthService;

        const userSubject = new BehaviorSubject(null);

        const authServiceMock = {
            userSubject,
            get userValue() {
                return userSubject.value;
            }
        };

        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    {provide: AuthService, useValue: authServiceMock}
                ],
            });
            authService = TestBed.inject(AuthService);
        });

        it("should add authorization header when user is logged in", () => {
            const user: User = {username: "username", password: "password"};
            authService.userSubject.next(user);

            const request = new HttpRequest("GET", environment.apiUrl + "/test");

            const next = jasmine.createSpy("next").and.callFake((req: HttpRequest<any>) => {
                const headers = req.headers.get("Authorization");
                expect(headers).toBe(`Basic ${btoa(user.username + ":" + user.password)}`);
                return of({type: HttpEventType.Response});
            });

            TestBed.runInInjectionContext(() => httpInterceptor(request, next)
                .subscribe(event => {
                    expect(event.type).toBe(HttpEventType.Response);
                }));
        });

        it("should not add authorization header when user is not logged in", () => {
            authService.userSubject.next(null);

            const request = new HttpRequest("GET", environment.apiUrl + "/test");

            const next = jasmine.createSpy("next").and.callFake((req: HttpRequest<any>) => {
                const headers = req.headers.get("Authorization");
                expect(headers).toBeNull();
                return of({type: HttpEventType.Response});
            });

            TestBed.runInInjectionContext(() => httpInterceptor(request, next)
                .subscribe(event => {
                    expect(event.type).toBe(HttpEventType.Response);
                }));
        });

        it("should not add authorization header when request is not to the API URL", () => {
            const user: User = {username: "username", password: "password"};
            authService.userSubject.next(user);

            const request = new HttpRequest("GET", "notTheApiURl/test");

            const next = jasmine.createSpy("next").and.callFake((req: HttpRequest<any>) => {
                const headers = req.headers.get("Authorization");
                expect(headers).toBeNull();
                return of({type: HttpEventType.Response});
            });

            TestBed.runInInjectionContext(() => httpInterceptor(request, next)
                .subscribe(event => {
                    expect(event.type).toBe(HttpEventType.Response);
                }));
        });

    }
)
;
