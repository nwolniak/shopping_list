import {Component, OnInit} from "@angular/core";
import {AlertService, ListService} from "@app/_services";
import {NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {Item, ShoppingListView} from "@app/_models";
import {delay} from "rxjs";

const compare = (v1: any, v2: any) => (v1 < v2 ? -1 : v1 > v2 ? 1 : 0);

@Component({
  templateUrl: "list.component.html",
  imports: [
    NgIf,
    NgForOf,
    RouterLink
  ],
  standalone: true
})
export class ListComponent implements OnInit {

  shoppingListViews: ShoppingListView[] = [];
  isModifying: boolean = false;

  constructor(
    private listService: ListService,
    private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.listService.getAll()
      .subscribe(shoppingListViews => this.shoppingListViews = shoppingListViews);
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
          this.alertService.error(err);
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
          this.alertService.error(err);
        }
      });
  }

  deleteItem(shoppingListView: ShoppingListView, item: Item) {
    item.isModifying = true;
    const listId = shoppingListView.shoppingList.id;
    const itemId = item.id;
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
          this.alertService.error(err);
        }
      });
  }

}
