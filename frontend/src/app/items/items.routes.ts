import {Routes} from "@angular/router";
import {ListComponent} from "@app/items/list.component";
import {AddComponent} from "@app/items/add.component";

export const ITEMS_ROUTES: Routes = [
  {path: "", component: ListComponent},
  {path: "shopping-lists/:listId/add-item", component: AddComponent}
]
