import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {User} from "@app/_models";
import {environment} from "@environments/environment";
import {AuthService} from "@app/_services/auth.service";
import {BehaviorSubject} from "rxjs";
import {Router} from "@angular/router";

describe("Authentication Service", () => {
    let service: AuthService;
    let httpTestingController: HttpTestingController;
    let router: Router;

    const userSubjectMock: BehaviorSubject<any> = new BehaviorSubject(null);

    const localStorageMock = {
        removeItem: (key: string) => {
            if (key === "user") {
                userSubjectMock.next(null);
            }
        },
        setItem: (key: string, value: string) => {
            if (key === "user") {
                userSubjectMock.next(value);
            }
        }
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [AuthService]
        });
        service = TestBed.inject(AuthService);
        httpTestingController = TestBed.inject(HttpTestingController);
        router = TestBed.inject(Router);
    });

    it("should register user", () => {
        const user: User = new User("username", "password");

        service.register(user)
            .subscribe(result => {
                expect(result).toEqual(user);
            });

        const testRequest = httpTestingController.expectOne(`${environment.registerUrl}`);
        expect(testRequest.request.method).toBe("POST");
        expect(testRequest.request.body).toBe(user);
        testRequest.flush(user);
    });

    it("should login user", () => {
        service.userSubject = userSubjectMock as BehaviorSubject<any>;
        spyOn(localStorage, "setItem").and.callFake(localStorageMock.setItem);

        const user: User = {
            username: "username",
            password: "password"
        }

        service.login(user)
            .subscribe(result => {
                expect(result).toEqual(user);
            });

        const testRequest = httpTestingController.expectOne(`${environment.loginUrl}`);
        expect(testRequest.request.method).toBe("POST");
        expect(testRequest.request.body).toBe(user);
        testRequest.flush(user);
        expect(localStorage.setItem).toHaveBeenCalledWith("user", JSON.stringify(user));
    });

    it("should logout user", () => {
        service.userSubject = userSubjectMock as BehaviorSubject<any>;
        spyOn(localStorage, "removeItem").and.callFake(localStorageMock.removeItem);
        spyOn(router, "navigate")

        service.logout();

        expect(localStorage.removeItem).toHaveBeenCalledWith("user");
        expect(router.navigate).toHaveBeenCalledWith(["/account/login"]);
    });

});
