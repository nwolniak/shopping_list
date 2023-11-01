export class Item {
  constructor(
    public name: string,
    public shoppingListId: number,
    public id: number,
    public isModifying: boolean = false
  ) {
  }

}
