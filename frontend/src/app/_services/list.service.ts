import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {AuthService} from "@app/_services/auth.service";
import {Item, ShoppingList, ShoppingListView} from "@app/_models";
import {environment} from "@environments/environment";
import {BehaviorSubject, map, Observable} from "rxjs";

@Injectable({providedIn: "root"})
export class ListService {

  private ordersSubject: BehaviorSubject<ShoppingListView[] | undefined>;
  private _shoppingLists: Observable<ShoppingListView[] | undefined>;

  constructor(
    private http: HttpClient,
    private auth: AuthService
  ) {
    this.ordersSubject = new BehaviorSubject<ShoppingListView[] | undefined>(undefined);
    this._shoppingLists = this.ordersSubject.asObservable();
  }

  getById(listId: string): Observable<ShoppingListView> {
    return this.http.get<ShoppingListView>(`${environment.apiUrl}/shopping-lists/${listId}`);
  }

  getAll(): Observable<ShoppingListView[]> {
    return this.http.get<ShoppingListView[]>(`${environment.apiUrl}/shopping-lists`)
      .pipe(
        map(shoppingLists => {
          this.ordersSubject.next(shoppingLists);
          return shoppingLists;
        })
      );
  }

  createShoppingList(): Observable<ShoppingList> {
    return this.http.get<ShoppingList>(`${environment.apiUrl}/shopping-lists/create`);
  }

  deleteShoppingList(listId: number) {
    return this.http.delete(`${environment.apiUrl}/shopping-lists/${listId}`);
  }

  addItemToShoppingList(listId: string, item: Item): Observable<Item> {
    item.shoppingListId = parseInt(listId);
    return this.http.post<Item>(`${environment.apiUrl}/shopping-lists/${listId}/items`, item);
  }

  deleteItemWithinList(listId: number, itemId: number) {
    return this.http.delete(`${environment.apiUrl}/shopping-lists/${listId}/items/${itemId}`);
  }

  // postShoppingList(cartId: string): Observable<ShoppingList> {
  //   return this.http.post<ShoppingList>(`${environment.apiUrl}/orders`, {cartId})
  //     .pipe(
  //       concatMap(orderDto => this.mapDtoToShoppingList(orderDto)),
  //       map(order => {
  //         console.log(`Post order ${order}`)
  //         this.ordersValue?.push(order);
  //         this.ordersSubject.next(this.ordersValue);
  //         return order;
  //       })
  //     );
  // }
  //
  // deleteShoppingList(orderId: string) {
  //   return this.http.delete(`${environment.apiUrl}/orders/${orderId}`)
  //     .pipe(map(() => {
  //       console.log(`Deleted ${orderId} order`)
  //       const orders = this.ordersValue?.filter(order => order.orderId !== orderId);
  //       this.ordersSubject.next(orders);
  //     }));
  // }

  // private orderPrice(orderItems: ItemQuantity[]): number {
  //   return orderItems
  //     .map(orderItem => parseFloat((orderItem.quantity * parseFloat(orderItem.item.unitPrice)).toFixed(2)))
  //     .reduce((previousValue, currentValue) => previousValue + currentValue);
  // }

  get shoppingLists(): Observable<ShoppingListView[] | undefined> {
    return this._shoppingLists;
  }

  get shoppingListsValue(): ShoppingListView[] | undefined {
    return this.ordersSubject.value;
  }

}