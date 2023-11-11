import {ShoppingList} from "@app/_models/shopping.list";
import {Item} from "@app/_models/item";

export class ShoppingListView {

    constructor(
        public shoppingList: ShoppingList,
        public items: Item[],
        public isModifying: boolean = false
    ) {
    }

}
