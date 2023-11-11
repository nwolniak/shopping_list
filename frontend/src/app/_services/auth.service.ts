import {Injectable} from "@angular/core";
import {BehaviorSubject, map, Observable} from "rxjs";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {environment} from "@environments/environment";
import {User} from "@app/_models";

@Injectable({
    providedIn: "root"
})
export class AuthService {
    userSubject: BehaviorSubject<User | null>;
    private _user: Observable<User | null>;

    constructor(private router: Router, private http: HttpClient) {
        this.userSubject = new BehaviorSubject(JSON.parse(localStorage.getItem("user")!));
        this._user = this.userSubject.asObservable();
        if (this.userValue) {
            this.login(this.userValue)
                .subscribe();
        } else {
            localStorage.removeItem("user");
            this.userSubject.next(null);
        }
    }

    login(user: User): Observable<User> {
        return this.http.post<User>(`${environment.loginUrl}`, user)
            .pipe(map(user => {
                localStorage.setItem("user", JSON.stringify(user));
                this.userSubject.next(user);
                return user;
            }))
    }

    register(user: User): Observable<User> {
        return this.http.post<User>(`${environment.registerUrl}`, user);
    }

    logout() {
        localStorage.removeItem("user");
        this.userSubject.next(null);
        this.router.navigate(["/account/login"]);
    }

    public get userValue(): User | null {
        return this.userSubject.value;
    }

    public get user(): Observable<User | null> {
        return this._user;
    }

}
