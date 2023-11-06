import {Routes} from "@angular/router";
import {LoginComponent, RegisterComponent} from "@app/account";
import {authGuard} from "@app/_helpers";

const itemsModuleLazy = () => import("@app/items/items.routes").then(routes => routes.ITEMS_ROUTES);

export const APP_ROUTES: Routes = [
  {path: "", loadChildren: itemsModuleLazy, canActivate: [authGuard]},
  {path: "account/login", component: LoginComponent},
  {path: "account/register", component: RegisterComponent},
  {path: "**", redirectTo: ""}
];
