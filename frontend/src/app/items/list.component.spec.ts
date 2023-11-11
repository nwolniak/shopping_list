import {AlertService, ListService} from "@app/_services";
import {Observable, of, throwError} from "rxjs";
import {Item, ShoppingList, ShoppingListView} from "@app/_models";
import {fakeAsync, TestBed, tick} from "@angular/core/testing";
import {ReactiveFormsModule} from "@angular/forms";
import {ListComponent} from "@app/items/list.component";
import {MatDatepickerInputEvent} from "@angular/material/datepicker";
import any = jasmine.any;

describe("List Component", () => {
    let component: ListComponent;
    let listService: ListService;
    let alertService: AlertService;

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
                ListComponent,
                {provide: AlertService, useValue: alertServiceMock},
                {provide: ListService, useValue: listServiceMock},
            ],
        });
        component = TestBed.inject(ListComponent);
        listService = TestBed.inject(ListService);
        alertService = TestBed.inject(AlertService);

        spyOn(alertService, "success");
        spyOn(alertService, "error");
    });

    it("should initialize calendar min and max dates properly", () => {
        expect(component.minDate).toBeDefined();
        expect(component.maxDate).toBeDefined();
    });

    it("should retrieve shopping lists on init", () => {
        const expectedShoppingListsViews: ShoppingListView[] = [{
            shoppingList: new ShoppingList(0, ""),
            items: [],
            isModifying: false
        }, {
            shoppingList: new ShoppingList(1, ""),
            items: [],
            isModifying: false
        }];

        spyOn(listService, "getShoppingLists").and.returnValue(of(expectedShoppingListsViews));

        expect(component.shoppingListViews).toHaveSize(0);
        component.ngOnInit();
        expect(component.shoppingListViews).toEqual(expectedShoppingListsViews);
        expect(component.shoppingListViews).toHaveSize(expectedShoppingListsViews.length);
    });

    it("should create shopping list", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const expectedShoppingListView: ShoppingListView = new ShoppingListView(shoppingList, []);
        spyOn(listService, "createShoppingList").and.returnValue(of(shoppingList));

        expect(component.shoppingListViews).toHaveSize(0);
        component.createList();

        tick(200);

        expect(component.shoppingListViews).toContain(expectedShoppingListView);
        expect(component.shoppingListViews).toHaveSize(1);
        expect(alertService.success).toHaveBeenCalled();
    }));

    it("should handle create shopping list error", fakeAsync(() => {
        const error = new Error("Failed");
        spyOn(listService, "createShoppingList").and.returnValue(throwError(() => error));

        component.createList();

        tick(200);

        expect(component.shoppingListViews).toHaveSize(0);
        expect(alertService.error).toHaveBeenCalledWith(error.message);
    }));

    it("should delete shopping list", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const shoppingListView: ShoppingListView = new ShoppingListView(shoppingList, []);
        spyOn(listService, "getShoppingLists").and.returnValue(of([shoppingListView]));
        spyOn(listService, "deleteShoppingList").and.returnValue(of(Object));

        component.ngOnInit();
        expect(component.shoppingListViews).toHaveSize(1);
        component.deleteList(shoppingListView);

        tick(200);

        expect(component.shoppingListViews).not.toContain(shoppingListView);
        expect(component.shoppingListViews).toHaveSize(0);
        expect(alertService.success).toHaveBeenCalled();
    }));

    it("should handle delete shopping list error", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const shoppingListView: ShoppingListView = new ShoppingListView(shoppingList, []);
        const error = new Error("Failed");
        spyOn(listService, "deleteShoppingList").and.returnValue(throwError(() => error));

        any(String)
        component.deleteList(shoppingListView);

        tick(200);

        expect(alertService.error).toHaveBeenCalledWith(error.message);
    }));

    it("should realize item within shopping list", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const item: Item = new Item("apple", 1, "Kg");
        const itemUpdated: Item = {
            name: "apple",
            units: 1,
            unitType: "Kg",
            isRealized: true,
            isModifying: true
        };
        const shoppingListView: ShoppingListView = new ShoppingListView(shoppingList, [item]);
        spyOn(listService, "getShoppingLists").and.returnValue(of([shoppingListView]));
        spyOn(listService, "updateItemWithinList").and.returnValue(of(itemUpdated));

        component.ngOnInit();
        expect(component.shoppingListViews).toHaveSize(1);
        component.realizeItem(shoppingListView, item);

        tick(200);

        expect(component.shoppingListViews).toHaveSize(1);
        expect(component.shoppingListViews[0].items[0].isRealized).toBeTruthy();
        expect(alertService.success).toHaveBeenCalled();
    }));

    it("should handle realize item within shopping list error", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const item: Item = new Item("apple", 1, "Kg");
        const shoppingListView: ShoppingListView = new ShoppingListView(shoppingList, [item]);
        const error = new Error("Failed");
        spyOn(listService, "updateItemWithinList").and.returnValue(throwError(() => error));

        component.realizeItem(shoppingListView, item);

        tick(200);

        expect(alertService.error).toHaveBeenCalledWith(error.message);
    }));

    it("should delete item within shopping list", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const item: Item = new Item("apple", 1, "Kg");
        const shoppingListView: ShoppingListView = new ShoppingListView(shoppingList, [item]);
        spyOn(listService, "getShoppingLists").and.returnValue(of([shoppingListView]));
        spyOn(listService, "deleteItemWithinList").and.returnValue(of(Object));

        component.ngOnInit();
        expect(component.shoppingListViews).toHaveSize(1);
        expect(component.shoppingListViews[0].items).toHaveSize(1);
        component.deleteItem(shoppingListView, item);

        tick(200);

        expect(component.shoppingListViews).toHaveSize(1);
        expect(component.shoppingListViews[0].items).toHaveSize(0);
        expect(component.shoppingListViews[0].items).not.toContain(item);
        expect(alertService.success).toHaveBeenCalled();
    }));

    it("should handle delete item within shopping list error", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const item: Item = new Item("apple", 1, "Kg");
        const shoppingListView: ShoppingListView = new ShoppingListView(shoppingList, [item]);
        const error = new Error("Failed");
        spyOn(listService, "deleteItemWithinList").and.returnValue(throwError(() => error));

        component.deleteItem(shoppingListView, item);

        tick(200);

        expect(alertService.error).toHaveBeenCalledWith(error.message);
    }));

    it("should change shopping list purchase date", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const shoppingListUpdated: ShoppingList = new ShoppingList(0, "2023-11-30 00:00:00+01:00");
        const shoppingListView: ShoppingListView = new ShoppingListView(shoppingList, []);
        spyOn(listService, "getShoppingLists").and.returnValue(of([shoppingListView]));
        spyOn(listService, "updateShoppingList").and.returnValue(of(shoppingListUpdated));

        const event = {
            value: new Date(2023, 10, 30),
        } as MatDatepickerInputEvent<Date>;

        component.ngOnInit();
        component.changePurchaseDate(shoppingList, event);

        tick(200);

        expect(listService.updateShoppingList).toHaveBeenCalledWith(shoppingListUpdated);
        expect(component.shoppingListViews).toHaveSize(1);
        expect(component.shoppingListViews[0].shoppingList.purchaseDate).toEqual("2023-11-30 00:00:00+01:00");
        expect(alertService.success).toHaveBeenCalled();
    }));

    it("should handle change shopping list purchase date error", fakeAsync(() => {
        const shoppingList: ShoppingList = new ShoppingList(0, "");
        const error = new Error("Failed");
        spyOn(listService, "updateShoppingList").and.returnValue(throwError(() => error));

        const event = {
            value: new Date(),
        } as MatDatepickerInputEvent<Date>;

        component.changePurchaseDate(shoppingList, event);

        tick(200);

        expect(alertService.error).toHaveBeenCalledWith(error.message);
    }));

});
