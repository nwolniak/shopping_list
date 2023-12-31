import {LoginComponent} from "@app/account";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {Observable} from "rxjs";
import {User} from "@app/_models";
import {RouterTestingModule} from "@angular/router/testing";
import {ActivatedRoute, provideRouter} from "@angular/router";
import {AuthService} from "@app/_services";

describe("[IT] Login Component", () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const authServiceMock = {
    register: Observable<User>
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      providers: [
        provideRouter([]),
        {provide: AuthService, useValue: authServiceMock},
        {provide: ActivatedRoute, useValue: {snapshot: {paramMap: new Map().set("", "")}}}
      ]
    });
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should render login form correctly", () => {
    const formElement = fixture.nativeElement.querySelector("form");
    expect(formElement).toBeTruthy();

    const usernameInput = formElement.querySelector("input[formControlName='username']");
    const passwordInput = formElement.querySelector("input[formControlName='password']");
    const loginButton = formElement.querySelector("button[type='submit']");

    expect(usernameInput).toBeTruthy();
    expect(passwordInput).toBeTruthy();
    expect(loginButton).toBeTruthy();

    expect(formElement.textContent).toContain("Username");
    expect(formElement.textContent).toContain("Password");
    expect(loginButton.textContent).toBe("Login ");
  });

  it("should not show any validation errors if form is submitted with correct data", () => {
    component.form.get("username")?.setValue("username");
    component.form.get("password")?.setValue("password");

    component.submitted = true;
    fixture.detectChanges();

    const errorMessages = fixture.nativeElement.querySelectorAll(".invalid-feedback");
    expect(errorMessages.length).toBe(0);
  });

  it("should show validation errors if form is submitted without data", () => {
    component.form.get("username")?.setErrors({required: true});
    component.form.get("password")?.setErrors({required: true});

    component.submitted = true;
    fixture.detectChanges();

    const errorMessages = fixture.nativeElement.querySelectorAll(".invalid-feedback");
    expect(errorMessages.length).toBe(2);

    expect(errorMessages[0].textContent).toContain("Username is required");
    expect(errorMessages[1].textContent).toContain("Password is required");
  });

  it("should show validation errors if form is submitted with incorrect data", () => {
    component.form.get("username")?.setErrors({required: false, minLength: true});
    component.form.get("password")?.setErrors({minLength: true});

    component.submitted = true;
    fixture.detectChanges();

    const errorMessages = fixture.nativeElement.querySelectorAll(".invalid-feedback");
    expect(errorMessages.length).toBe(2);

    expect(errorMessages[0].textContent).toContain("Username must be at least 6 characters");
    expect(errorMessages[1].textContent).toContain("Password must be at least 6 characters");
  });

  it("should disable the submit button while loading", () => {
    component.loading = true;
    fixture.detectChanges();
    const submit = fixture.nativeElement.querySelector("button[type='submit']");
    expect(submit.disabled).toBeTruthy();
  });

});
