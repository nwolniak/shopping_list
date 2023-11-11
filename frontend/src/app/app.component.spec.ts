import {AppComponent} from "@app/app.component";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {AuthService} from "@app/_services";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {User} from "@app/_models";
import {Observable, of} from "rxjs";

describe("App Component", () => {
    let component: AppComponent;
    let fixture: ComponentFixture<AppComponent>;
    let authService: AuthService;

    const authServiceMock = {
        user: of(new User("username", "password")) as Observable<User | null>,
        logout(): void {
        }
    } as AuthService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FontAwesomeModule],
            providers: [
                AppComponent,
                {provide: AuthService, useValue: authServiceMock}
            ]
        });
        authService = TestBed.inject(AuthService);
        fixture = TestBed.createComponent(AppComponent);
        component = fixture.componentInstance;


        fixture.detectChanges();
    });

    it("should create the app", () => {
        expect(component).toBeTruthy();
    });

    it("should call AuthService.logout() when logout is called", () => {
        spyOn(authService, "logout");
        component.logout();
        expect(authService.logout).toHaveBeenCalled();
    });
});
