import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {TestBed} from '@angular/core/testing';
import {Item, ShoppingList, ShoppingListView} from "@app/_models";
import {ListService} from "@app/_services";
import {environment} from "@environments/environment";

describe("ListService", () => {
    let service: ListService;
    let httpTestingController: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ListService]
        });
        service = TestBed.inject(ListService);
        httpTestingController = TestBed.inject(HttpTestingController);
    });

    it("should get shopping list by id", () => {
        const listId = 0;
        const expectedShoppingListView: ShoppingListView = {
            shoppingList: new ShoppingList(0, ""),
            items: [],
            isModifying: false
        };

        service.getShoppingList(listId)
            .subscribe(result => {
                expect(result).toEqual(expectedShoppingListView);
            });

        const testRequest = httpTestingController.expectOne(`${environment.apiUrl}/shopping-lists/${listId}`);
        expect(testRequest.request.method).toBe("GET");
        testRequest.flush(expectedShoppingListView);
    });

    it("should get shopping lists", () => {
        const expectedShoppingListsViews: ShoppingListView[] = [{
            shoppingList: new ShoppingList(0, ""),
            items: [],
            isModifying: false
        }, {
            shoppingList: new ShoppingList(1, ""),
            items: [],
            isModifying: false
        }];

        service.getShoppingLists()
            .subscribe(result => {
                expect(result).toEqual(expectedShoppingListsViews);
            });

        const testRequest = httpTestingController.expectOne(`${environment.apiUrl}/shopping-lists`);
        expect(testRequest.request.method).toBe("GET");
        testRequest.flush(expectedShoppingListsViews);
    })

    it("should create shopping list", () => {
        const expectedShoppingList: ShoppingList = {
            id: 0,
            purchaseDate: ""
        };

        service.createShoppingList()
            .subscribe(result => {
                expect(result).toEqual(expectedShoppingList);
            });

        const testRequest = httpTestingController.expectOne(`${environment.apiUrl}/shopping-lists/create`);
        expect(testRequest.request.method).toBe("GET");
        testRequest.flush(expectedShoppingList);
    })

    it("should update shopping list", () => {
        const listId = 0;
        const expectedShoppingList: ShoppingList = {
            id: 0,
            purchaseDate: ""
        };

        service.updateShoppingList(expectedShoppingList)
            .subscribe(result => {
                expect(result).toEqual(expectedShoppingList);
            });

        const testRequest = httpTestingController.expectOne(`${environment.apiUrl}/shopping-lists/${expectedShoppingList.id}`);
        expect(testRequest.request.method).toBe("PUT");
        expect(testRequest.request.body).toBe(expectedShoppingList);
        testRequest.flush(expectedShoppingList);
    })

    it("should delete shopping list by id", () => {
        const listId = 0;

        service.deleteShoppingList(listId).subscribe();

        const testRequest = httpTestingController.expectOne(`${environment.apiUrl}/shopping-lists/${listId}`);
        expect(testRequest.request.method).toBe("DELETE");
        testRequest.flush(null, {statusText: "OK"});
    })

    it("should add item to shopping list", () => {
        const listId = 0;
        const expectedItem: Item = new Item(
            "apple",
            1,
            "Kg"
        );

        service.addItemToShoppingList(listId, expectedItem)
            .subscribe(result => {
                expect(result).toEqual(expectedItem);
            });

        const testRequest = httpTestingController.expectOne(`${environment.apiUrl}/shopping-lists/${listId}/items`);
        expect(testRequest.request.method).toBe("POST");
        expect(testRequest.request.body).toBe(expectedItem);
        testRequest.flush(expectedItem);
    })

    it("should update item within shopping list", () => {
        const listId = 0;
        const expectedItem: Item = new Item(
            "apple",
            1,
            "Kg"
        );

        service.updateItemWithinList(listId, expectedItem)
            .subscribe(result => {
                expect(result).toEqual(expectedItem);
            });

        const testRequest = httpTestingController.expectOne(`${environment.apiUrl}/shopping-lists/${listId}/items/${expectedItem.id}`);
        expect(testRequest.request.method).toBe("PUT");
        expect(testRequest.request.body).toBe(expectedItem);
        testRequest.flush(expectedItem);
    })

    it("should delete item within shopping list", () => {
        const listId = 0;
        const itemId = 0;

        service.deleteItemWithinList(listId, itemId).subscribe();

        const testRequest = httpTestingController.expectOne(`${environment.apiUrl}/shopping-lists/${listId}/items/${itemId}`);
        expect(testRequest.request.method).toBe("DELETE");
        testRequest.flush(null, {statusText: "OK"});
    })

});
