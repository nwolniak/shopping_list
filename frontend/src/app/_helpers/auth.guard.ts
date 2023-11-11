import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from "@app/_services";


export function authGuard(): boolean {
    const router: Router = inject(Router);
    const auth: AuthService = inject(AuthService);

    if (!auth.userValue) {
        router.navigate(['/account/login']);
        return false;
    }
    return true;
}
