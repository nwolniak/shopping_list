import {AlertService, ListService} from "@app/_services";
import {ActivatedRoute, Router} from "@angular/router";
import {fakeAsync, TestBed, tick} from "@angular/core/testing";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {AddComponent} from "@app/items/add.component";
import {Observable, of, throwError} from "rxjs";
import {Item} from "@app/_models";

describe("Add Item Component", () => {
    let component: AddComponent;
    let listService: ListService;
    let alertService: AlertService;
    let router: Router;

    const listServiceMock = {
        addItemToShoppingList: Observable<Item>
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
                AddComponent,
                FormBuilder,
                AlertService,
                Router,
                {provide: AlertService, useValue: alertServiceMock},
                {provide: ListService, useValue: listServiceMock},
                {
                    provide: ActivatedRoute,
                    useValue: {
                        snapshot: {
                            params: {listId: "1"}
                        }
                    }
                }
            ],
        });
        component = TestBed.inject(AddComponent);
        router = TestBed.inject(Router);
        listService = TestBed.inject(ListService);
        alertService = TestBed.inject(AlertService);
    });

    it("should create item creation form on init", () => {
        expect(component.form).not.toBeDefined();
        component.ngOnInit();
        expect(component.form).toBeDefined();
        expect(component.form.contains("name")).toBeTruthy();
        expect(component.form.contains("units")).toBeTruthy();
        expect(component.form.contains("unitType")).toBeTruthy();
        expect(component.listId).toEqual(1);
    });

    it("#onSubmit should check if form is valid", fakeAsync(() => {
        spyOn(listService, "addItemToShoppingList");
        component.ngOnInit();
        spyOnProperty(component.form, "invalid").and.returnValue(true);
        component.onSubmit();
        tick();
        expect(listService.addItemToShoppingList).not.toHaveBeenCalled();
    }));

    it("#onSubmit should check if list id was specified", fakeAsync(() => {
        spyOn(listService, "addItemToShoppingList");
        component.ngOnInit();
        spyOnProperty(component.form, "invalid").and.returnValue(false);
        component.listId = undefined;
        component.onSubmit();
        tick();
        expect(listService.addItemToShoppingList).not.toHaveBeenCalled();
    }));

    it("#onSubmit should handle successful item addition", fakeAsync(() => {
        const item: Item = new Item(
            "apple",
            1,
            "Kg"
        );
        spyOn(listService, "addItemToShoppingList").and.returnValue(of(item));
        spyOn(router, "navigate");
        spyOn(alertService, "success");
        component.ngOnInit();
        component.form.setValue({
            name: item.name,
            units: item.units.toString(),
            unitType: item.unitType
        });
        component.onSubmit();
        tick();
        expect(listService.addItemToShoppingList).toHaveBeenCalledWith(1, item);
        expect(router.navigate).toHaveBeenCalledWith(["/shopping-lists"]);
        expect(alertService.success).toHaveBeenCalledWith("Item added", true);
    }));

    it("#onSubmit should handle item addition  error", fakeAsync(() => {
        const item: Item = new Item(
            "apple",
            1,
            "Kg"
        );
        const error = new Error("Failed");
        spyOn(listService, "addItemToShoppingList").and.returnValue(throwError(() => error));
        spyOn(router, "navigate");
        spyOn(alertService, "error");
        component.ngOnInit();
        component.form.setValue({
            name: item.name,
            units: item.units,
            unitType: item.unitType
        });
        component.onSubmit();
        tick();
        expect(listService.addItemToShoppingList).toHaveBeenCalledWith(1, item);
        expect(alertService.error).toHaveBeenCalledWith(error.message);
    }));

});
