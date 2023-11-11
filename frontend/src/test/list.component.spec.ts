import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {Observable, of} from "rxjs";
import {Item, ShoppingList, ShoppingListView} from "@app/_models";
import {RouterTestingModule} from "@angular/router/testing";
import {provideRouter} from "@angular/router";
import {AlertService, ListService} from "@app/_services";
import {ListComponent} from "@app/items";
import Spy = jasmine.Spy;

describe("[IT] Shopping List Component", () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let service: ListService;

  const listServiceMock = {
    getShoppingList: Observable<ShoppingListView>,
    getShoppingLists: Observable<ShoppingListView[]>,
    createShoppingList: Observable<ShoppingList>,
    updateShoppingList: Observable<ShoppingList>,
    deleteShoppingList: Observable<Object>,
    addItemToShoppingList: Observable<Item>,
    updateItemWithinList: Observable<Item>,
    deleteItemWithinList: Observable<Object>,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      providers: [
        provideRouter([]),
        AlertService,
        {provide: ListService, useValue: listServiceMock}
      ]
    });
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(ListService);

    const expectedShoppingListsViews: ShoppingListView[] = [{
      shoppingList: new ShoppingList(0, ""),
      items: [new Item("apple", 1, "Kg")],
      isModifying: false
    }, {
      shoppingList: new ShoppingList(1, ""),
      items: [],
      isModifying: false
    }];
    spyOn(service, "getShoppingLists").and.returnValue(of(expectedShoppingListsViews));
    fixture.detectChanges();
  });

  it("should render empty view when no data is available", () => {
    const shoppingListViews: ShoppingListView[] = [];
    (service.getShoppingLists as Spy).and.returnValue(of(shoppingListViews));
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(ListService);

    fixture.detectChanges();

    const h1 = fixture.nativeElement.querySelector("h1");
    const createListElement = fixture.nativeElement.querySelector(".btn");

    expect(h1).toBeTruthy();
    expect(h1.innerText).toContain("Shopping Lists");
    expect(createListElement).toBeTruthy();
    expect(createListElement.disabled).toBeFalsy();
    expect(createListElement.innerText).toContain("Add List");

    const cards = fixture.nativeElement.querySelectorAll(".card");
    expect(cards.length).toBe(0);
  });

  it("should render shopping lists when data is available", () => {
    const cards = Array.from(fixture.nativeElement.querySelectorAll(".card"));
    expect(cards).toHaveSize(2);
    const card = fixture.nativeElement.querySelector(".card");
    expect(card.innerText).toContain("Shopping List ID:");
    expect(card.innerText).toContain("Name");
    expect(card.innerText).toContain("Units");
    expect(card.innerText).toContain("Unit Type");
  });

  it("should create shopping list view when add list button is clicked", fakeAsync(() => {
    const shoppingList = new ShoppingList(2, "");
    spyOn(service, "createShoppingList").and.returnValue(of(shoppingList));

    const createListElement = fixture.nativeElement.querySelector(".btn");

    createListElement.click();
    tick(200);
    fixture.detectChanges();

    const cards = fixture.nativeElement.querySelectorAll(".card");
    expect(cards.length).toBe(3);
  }));

  it("should delete shopping list view when delete button is clicked", fakeAsync(() => {
    spyOn(service, "deleteShoppingList").and.returnValue(of(Object));

    const deleteListElement = fixture.nativeElement.querySelector(".delete-button");

    deleteListElement.click();
    tick(200);
    fixture.detectChanges();

    const cards = fixture.nativeElement.querySelectorAll(".card");
    expect(cards.length).toBe(1);
  }));

  it("should change item row color when buy button is clicked", fakeAsync(() => {
    const itemUpdated: Item = {
      name: "apple",
      units: 1,
      unitType: "Kg",
      isRealized: true,
      isModifying: false
    }
    spyOn(service, "updateItemWithinList").and.returnValue(of(itemUpdated));

    const rowNotChanged = fixture.nativeElement.querySelector(".table-success");
    expect(rowNotChanged).toBeNull();

    const realizeItemElement = fixture.nativeElement.querySelector(".realize-button");

    realizeItemElement.click();
    tick(200);
    fixture.detectChanges();

    const rowChanged = fixture.nativeElement.querySelector(".table-success");
    expect(rowChanged.textContent).toContain("apple");
    expect(rowChanged.textContent).toContain("1");
    expect(rowChanged.textContent).toContain("Kg");
  }));

  it("should delete item row when item delete button is clicked", fakeAsync(() => {
    spyOn(service, "deleteItemWithinList").and.returnValue(of(Object));

    const deleteItemElement = fixture.nativeElement.querySelector(".delete-item-button");

    deleteItemElement.click();
    tick(200);
    fixture.detectChanges();

    const deleted = fixture.nativeElement.querySelector(".delete-item-button");
    expect(deleted).toBeNull();
  }));

});
