import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Item, ShoppingList, ShoppingListView} from "@app/_models";
import {environment} from "@environments/environment";
import {Observable} from "rxjs";

@Injectable({providedIn: "root"})
export class ListService {

    constructor(private http: HttpClient) {
    }

    getShoppingList(listId: number): Observable<ShoppingListView> {
        return this.http.get<ShoppingListView>(`${environment.apiUrl}/shopping-lists/${listId}`);
    }

    getShoppingLists(): Observable<ShoppingListView[]> {
        return this.http.get<ShoppingListView[]>(`${environment.apiUrl}/shopping-lists`);
    }

    createShoppingList(): Observable<ShoppingList> {
        return this.http.get<ShoppingList>(`${environment.apiUrl}/shopping-lists/create`);
    }

    updateShoppingList(shoppingList: ShoppingList): Observable<ShoppingList> {
        return this.http.put<ShoppingList>(`${environment.apiUrl}/shopping-lists/${shoppingList.id}`, shoppingList);
    }

    deleteShoppingList(listId: number) {
        return this.http.delete(`${environment.apiUrl}/shopping-lists/${listId}`);
    }

    addItemToShoppingList(listId: number, item: Item): Observable<Item> {
        item.shoppingListId = listId;
        return this.http.post<Item>(`${environment.apiUrl}/shopping-lists/${listId}/items`, item);
    }

    updateItemWithinList(listId: number, item: Item): Observable<Item> {
        return this.http.put<Item>(`${environment.apiUrl}/shopping-lists/${listId}/items/${item.id}`, item);
    }

    deleteItemWithinList(listId: number, itemId: number) {
        return this.http.delete(`${environment.apiUrl}/shopping-lists/${listId}/items/${itemId}`);
    }

}
