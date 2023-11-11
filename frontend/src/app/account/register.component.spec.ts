import {RegisterComponent} from "@app/account/register.component";
import {AlertService, AuthService} from "@app/_services";
import {Router} from "@angular/router";
import {fakeAsync, TestBed, tick} from "@angular/core/testing";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {BehaviorSubject, Observable, of, throwError} from "rxjs";
import {User} from "@app/_models";

describe("Register Component", () => {
    let component: RegisterComponent;
    let authService: AuthService;
    let router: Router;
    let alertService: AlertService;

    const userSubject = new BehaviorSubject(null);

    const authServiceMock = {
        userSubject,
        get userValue() {
            return userSubject.value;
        },
        register: Observable<User>
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
                RegisterComponent,
                FormBuilder,
                AuthService,
                AlertService,
                Router,
                {provide: AuthService, useValue: authServiceMock},
                {provide: AlertService, useValue: alertServiceMock}
            ],
        });
        router = TestBed.inject(Router);
        component = TestBed.inject(RegisterComponent);
        authService = TestBed.inject(AuthService);
        authService.userSubject.next(null);
        alertService = TestBed.inject(AlertService);
    });

    it("should create registration form on init", () => {
        expect(component.form).not.toBeDefined();
        component.ngOnInit();
        expect(component.form).toBeDefined();
        expect(component.form.contains("username")).toBeTruthy();
        expect(component.form.contains("password")).toBeTruthy();
    });

    it("should not navigate to the root component when user is not logged in", fakeAsync(() => {
        spyOn(router, "navigate");
        component = TestBed.inject(RegisterComponent);
        tick();
        expect(router.navigate).not.toHaveBeenCalled();
    }));

    it("#onSubmit should handle invalid form", fakeAsync(() => {
        spyOn(authService, "register");
        component.ngOnInit();
        spyOnProperty(component.form, "invalid").and.returnValue(true);
        component.onSubmit();
        tick();
        expect(authService.register).not.toHaveBeenCalled();
    }));

    it("#onSubmit should handle successful registration", fakeAsync(() => {
        const user: User = {username: "username", password: "password"};
        spyOn(authService, "register").and.returnValue(of(user));
        spyOn(router, "navigate");
        component.ngOnInit();
        component.form.setValue(user);
        component.onSubmit();
        tick();
        expect(authService.register).toHaveBeenCalledWith(user);
        expect(router.navigate).toHaveBeenCalledWith(["account/login"]);
    }));

    it("#onSubmit should handle registration error", fakeAsync(() => {
        const user: User = {username: "username", password: "password"};
        const error = new Error("Failed");
        spyOn(authService, "register").and.returnValue(throwError(() => error));
        spyOn(alertService, "error");
        component.ngOnInit();
        component.form.setValue(user);
        component.onSubmit();
        tick();
        expect(authService.register).toHaveBeenCalledWith(user);
        expect(alertService.error).toHaveBeenCalledWith(error.message);
    }));


});
