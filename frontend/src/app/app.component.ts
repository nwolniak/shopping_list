import {Component} from '@angular/core';
import {AuthService} from "src/app/_services";
import {FaIconLibrary, FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {faChevronLeft, faEye, faPencilSquare, faUser} from "@fortawesome/free-solid-svg-icons";
import {RouterOutlet} from "@angular/router";
import {AlertComponent} from "@app/components/alert.component";
import {CommonModule} from "@angular/common";
import {User} from "@app/_models";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    imports: [
        FontAwesomeModule,
        RouterOutlet,
        AlertComponent,
        CommonModule
    ],
    standalone: true
})
export class AppComponent {

    user?: User | null;

    constructor(library: FaIconLibrary,
                private auth: AuthService) {
        this.auth.user.subscribe(user => this.user = user);
        library.addIcons(faEye, faPencilSquare, faUser, faChevronLeft);
    }

    logout() {
        this.auth.logout();
    }

}
