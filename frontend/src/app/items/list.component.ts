import {Component, OnInit} from "@angular/core";
import {AlertService, ListService} from "@app/_services";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {Item, ShoppingList, ShoppingListView} from "@app/_models";
import {delay} from "rxjs";
import {MatDatepickerInputEvent, MatDatepickerModule} from "@angular/material/datepicker";
import {MatInputModule} from "@angular/material/input";
import {MatNativeDateModule} from "@angular/material/core";
import {FormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {format} from "date-fns-tz";

@Component({
    templateUrl: "list.component.html",
    imports: [
        NgForOf,
        RouterLink,
        NgIf,
        MatDatepickerModule,
        MatInputModule,
        NgClass,
        DatePipe,
        MatNativeDateModule,
        FormsModule,
        MatButtonModule
    ],
    standalone: true
})
export class ListComponent implements OnInit {

    shoppingListViews: ShoppingListView[] = [];
    isModifying: boolean = false;
    minDate: Date;
    maxDate: Date;

    constructor(
        private listService: ListService,
        private alertService: AlertService) {
        this.minDate = new Date();
        this.maxDate = new Date();
        this.maxDate.setDate(this.maxDate.getDate() + 30);
    }

    ngOnInit(): void {
        this.listService.getShoppingLists()
            .subscribe(shoppingListViews => {
                this.shoppingListViews = shoppingListViews
            });
    }

    createList() {
        this.isModifying = true;
        this.listService.createShoppingList()
            .pipe(delay(200))
            .subscribe({
                next: shoppingList => {
                    this.shoppingListViews.push(new ShoppingListView(shoppingList, []));
                    this.isModifying = false;
                    this.alertService.success("List created");
                },
                error: err => {
                    this.isModifying = false;
                    this.alertService.error(err.message);
                }
            });
    }

    deleteList(shoppingListView: ShoppingListView) {
        shoppingListView.isModifying = true;
        const listId = shoppingListView.shoppingList.id;
        this.listService.deleteShoppingList(listId)
            .pipe(delay(200))
            .subscribe({
                next: () => {
                    this.shoppingListViews = this.shoppingListViews.filter(view => view.shoppingList.id !== listId);
                    this.alertService.success("List deleted");
                },
                error: err => {
                    shoppingListView.isModifying = false;
                    this.alertService.error(err.message);
                }
            });
    }

    realizeItem(shoppingListView: ShoppingListView, item: Item) {
        item.isModifying = true;
        let itemToUpdate = {...item};
        itemToUpdate.isRealized = true;
        const listId = shoppingListView.shoppingList.id;
        const itemId = itemToUpdate.id!;
        this.listService.updateItemWithinList(listId, itemToUpdate)
            .pipe(delay(200))
            .subscribe({
                next: itemReturned => {
                    item.isModifying = false;
                    this.shoppingListViews = this.shoppingListViews.map(view => {
                        if (view.shoppingList.id === listId) {
                            let idx = view.items.findIndex(item => item.id === itemId);
                            view.items[idx] = itemReturned
                        }
                        return view;
                    })
                    this.alertService.success("Item realized");
                },
                error: err => {
                    item.isModifying = false;
                    this.alertService.error(err.message);
                }
            });
    }

    deleteItem(shoppingListView: ShoppingListView, item: Item) {
        item.isModifying = true;
        const listId = shoppingListView.shoppingList.id;
        const itemId = item.id!;
        this.listService.deleteItemWithinList(listId, itemId)
            .pipe(delay(200))
            .subscribe({
                next: () => {
                    item.isModifying = false;
                    this.shoppingListViews = this.shoppingListViews.map(view => {
                        if (view.shoppingList.id === listId) {
                            view.items = view.items.filter(item => item.id !== itemId);
                        }
                        return view;
                    })
                    this.alertService.success("Item deleted");
                },
                error: err => {
                    item.isModifying = false;
                    this.alertService.error(err.message);
                }
            });
    }

    changePurchaseDate(shoppingList: ShoppingList, event: MatDatepickerInputEvent<Date>) {
        shoppingList.purchaseDate = format(event.value!, "yyyy-MM-dd HH:mm:ssXXX", {timeZone: "Europe/Warsaw"});
        this.listService.updateShoppingList(shoppingList)
            .pipe(delay(200))
            .subscribe({
                next: shoppingListReturned => {
                    this.shoppingListViews = this.shoppingListViews.map(view => {
                        if (view.shoppingList.id === shoppingListReturned.id) {
                            view.shoppingList = shoppingListReturned;
                        }
                        return view;
                    })
                    this.alertService.success("Purchase date changed");
                },
                error: err => {
                    this.alertService.error(err.message);
                }
            });
    }

}
