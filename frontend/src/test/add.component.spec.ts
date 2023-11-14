import {ComponentFixture, TestBed} from "@angular/core/testing";
import {Observable, of} from "rxjs";
import {Item} from "@app/_models";
import {RouterTestingModule} from "@angular/router/testing";
import {ActivatedRoute, provideRouter} from "@angular/router";
import {AddComponent} from "@app/items";
import {ListService} from "@app/_services";

describe("[IT] Add Item Component", () => {
  let component: AddComponent;
  let fixture: ComponentFixture<AddComponent>;
  let service: ListService;

  const listServiceMock = {
    addItemToShoppingList: Observable<Item>
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      providers: [
        provideRouter([]),
        {provide: ListService, useValue: listServiceMock},
        {provide: ActivatedRoute, useValue: {snapshot: {paramMap: new Map().set("", "")}}},
        {provide: ActivatedRoute, useValue: {snapshot: {params: {listId: "1"}}}}
      ]
    });
    fixture = TestBed.createComponent(AddComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(ListService);
    fixture.detectChanges();
  });

  it("should render item creation form correctly", () => {
    const formElement = fixture.nativeElement.querySelector("form");
    expect(formElement).toBeTruthy();

    const nameInput = formElement.querySelector("input[formControlName='name']");
    const unitsInput = formElement.querySelector("input[formControlName='units']");
    const unitTypeInput = formElement.querySelector("select[formControlName='unitType']");
    const submitButton = formElement.querySelector("button[type='submit']");

    expect(nameInput).toBeTruthy();
    expect(unitsInput).toBeTruthy();
    expect(unitTypeInput).toBeTruthy();
    expect(submitButton).toBeTruthy();

    const options: HTMLOptionsCollection = unitTypeInput.options;
    const values = Array.from(options).map(opt => opt.value)
    expect(values).toContain("Szt")
    expect(values).toContain("Kg")
    expect(values).toContain("L")

    expect(formElement.textContent).toContain("Name");
    expect(formElement.textContent).toContain("Units");
    expect(formElement.textContent).toContain("Unit Type");
    expect(submitButton.textContent).toBe("Submit ");
  });

  it("should not show any validation errors if form is submitted with correct data", () => {
    const item = new Item("apple", 1, "Kg");
    spyOn(service, "addItemToShoppingList").and.returnValue(of(item));
    const formElement = fixture.nativeElement.querySelector("form");
    const nameInput = formElement.querySelector("input[formControlName='name']");
    const unitsInput = formElement.querySelector("input[formControlName='units']");
    const unitTypeInput = formElement.querySelector("select[formControlName='unitType']");
    const submitButton = formElement.querySelector("button[type='submit']");

    nameInput.value = "apple";
    unitsInput.value = 1;
    unitTypeInput.value = unitTypeInput.options[2].value;

    nameInput.dispatchEvent(new Event("input"));
    unitsInput.dispatchEvent(new Event("input"));
    unitTypeInput.dispatchEvent(new Event("change"));

    submitButton.click();

    fixture.detectChanges();

    const errorMessages = fixture.nativeElement.querySelectorAll(".invalid-feedback");
    expect(errorMessages.length).toBe(0);
    expect(service.addItemToShoppingList).toHaveBeenCalled();
  });

  it("should show validation errors if form is submitted without data", () => {
    spyOn(service, "addItemToShoppingList");
    const formElement = fixture.nativeElement.querySelector("form");
    const submitButton = formElement.querySelector("button[type='submit']");

    submitButton.click();

    fixture.detectChanges();

    const errorMessages = fixture.nativeElement.querySelectorAll(".invalid-feedback");
    expect(errorMessages.length).toBe(3);

    expect(errorMessages[0].textContent).toContain("Name field is required");
    expect(errorMessages[1].textContent).toContain("Units field is required");
    expect(errorMessages[2].textContent).toContain("Unit Type is required");
    expect(service.addItemToShoppingList).not.toHaveBeenCalled();
  });

  it("should show validation errors if form is submitted with incorrect data", () => {
    spyOn(service, "addItemToShoppingList");
    const formElement = fixture.nativeElement.querySelector("form");
    const nameInput = formElement.querySelector("input[formControlName='name']");
    const unitsInput = formElement.querySelector("input[formControlName='units']");
    const unitTypeInput = formElement.querySelector("select[formControlName='unitType']");
    const submitButton = formElement.querySelector("button[type='submit']");

    nameInput.value = "123";
    unitsInput.value = "o";
    unitTypeInput.value = unitTypeInput.options[2].value;

    nameInput.dispatchEvent(new Event("input"));
    unitsInput.dispatchEvent(new Event("input"));
    unitTypeInput.dispatchEvent(new Event("change"));

    submitButton.click();

    fixture.detectChanges();

    const errorMessages = fixture.nativeElement.querySelectorAll(".invalid-feedback");
    expect(errorMessages.length).toBe(2);
    expect(errorMessages[0].textContent).toContain("Name field should be characters only");
    expect(errorMessages[1].textContent).toContain("Units field should be numbers only");
    expect(service.addItemToShoppingList).not.toHaveBeenCalled();
  });

  it("should disable the submit button while submitting", () => {
    component.submitting = true;
    fixture.detectChanges();
    const submit = fixture.nativeElement.querySelector("button[type='submit']");
    expect(submit.disabled).toBeTruthy();
  });

});
