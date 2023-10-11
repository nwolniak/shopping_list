import {Routes} from "@angular/router";
import {HomeComponent} from "./home";

export const APP_ROUTES: Routes = [
  {path: "", component: HomeComponent},
  {path: "**", redirectTo: ""}
];
