import {AlertService, AuthService} from "@app/_services";
import {Router} from "@angular/router";
import {fakeAsync, TestBed, tick} from "@angular/core/testing";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {BehaviorSubject, Observable, of, throwError} from "rxjs";
import {User} from "@app/_models";
import {LoginComponent} from "@app/account/login.component";

describe("Login Component", () => {
    let component: LoginComponent;
    let authService: AuthService;
    let router: Router;
    let alertService: AlertService;

    const userSubject = new BehaviorSubject(null);

    const authServiceMock = {
        userSubject,
        get userValue() {
            return userSubject.value;
        },
        login: Observable<User>
    };

    const alertServiceMock = {
        clear() {
        },
        success() {
        },
        error() {
        }
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [ReactiveFormsModule],
            providers: [
                LoginComponent,
                FormBuilder,
                AuthService,
                AlertService,
                Router,
                {provide: AuthService, useValue: authServiceMock},
                {provide: AlertService, useValue: alertServiceMock}
            ],
        });
        router = TestBed.inject(Router);
        component = TestBed.inject(LoginComponent);
        authService = TestBed.inject(AuthService);
        authService.userSubject.next(null);
        alertService = TestBed.inject(AlertService);
    });

    it("should not navigate to the root component when user is not logged in", fakeAsync(() => {
        spyOn(router, "navigate");
        component = TestBed.inject(LoginComponent);
        tick();
        expect(router.navigate).not.toHaveBeenCalled();
    }));

    it("should create login form on init", () => {
        expect(component.form).not.toBeDefined();
        component.ngOnInit();
        expect(component.form).toBeDefined();
        expect(component.form.contains("username")).toBeTruthy();
        expect(component.form.contains("password")).toBeTruthy();
    });

    it("#onSubmit should handle invalid form", fakeAsync(() => {
        spyOn(authService, "login");
        component.ngOnInit();
        spyOnProperty(component.form, "invalid").and.returnValue(true);
        component.onSubmit();
        tick();
        expect(authService.login).not.toHaveBeenCalled();
    }));

    it("#onSubmit should handle successful login", fakeAsync(() => {
        const user: User = {username: "username", password: "password"};
        spyOn(authService, "login").and.returnValue(of(user));
        spyOn(router, "navigate");
        component.ngOnInit();
        component.form.setValue(user);
        component.onSubmit();
        tick();
        expect(authService.login).toHaveBeenCalledWith(user);
        expect(router.navigate).toHaveBeenCalledWith(["/account/register"]);
    }));

    it("#onSubmit should handle login error", fakeAsync(() => {
        const user: User = {username: "username", password: "password"};
        const error = new Error("Failed");
        spyOn(authService, "login").and.returnValue(throwError(() => error));
        spyOn(alertService, 'error');
        component.ngOnInit();
        component.form.setValue(user);
        component.onSubmit();
        tick();
        expect(authService.login).toHaveBeenCalledWith(user);
        expect(alertService.error).toHaveBeenCalledWith(error.message);
    }));


});
