export class Item {

  public id?: number;
  public isRealized: boolean = false;
  public isModifying: boolean = false;

  constructor(
    public name: string,
    public units: number,
    public unitType: string,
    public shoppingListId: number,
  ) {
  }

}
