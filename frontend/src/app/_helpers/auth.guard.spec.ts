import {AuthService} from "@app/_services";
import {Router} from "@angular/router";
import {BehaviorSubject} from "rxjs";
import {User} from "@app/_models";
import {authGuard} from "@app/_helpers/auth.guard";
import {TestBed} from "@angular/core/testing";

describe("Authentication Guard", () => {
    let router: Router;
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
                {provide: AuthService, useValue: authServiceMock},
                {provide: Router, useValue: {navigate: jasmine.createSpy('navigate')}},
            ],
        });
        authService = TestBed.inject(AuthService);
        router = TestBed.inject(Router);
    });

    it("should allow access when the user is logged in", () => {
        const user: User = {username: "username", password: "password"};
        authService.userSubject.next(user);

        const result = TestBed.runInInjectionContext(authGuard);

        expect(result).toBeTrue();
        expect(router.navigate).not.toHaveBeenCalled();
    });

    it("should deny access and navigate to login page when user is not logged in", () => {
        authService.userSubject.next(null);
        const result = TestBed.runInInjectionContext(authGuard);

        expect(result).toBeFalse();
        expect(router.navigate).toHaveBeenCalledWith(["/account/login"]);
    });
});
