import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Router, RouterLink} from "@angular/router";
import {AlertService, AuthService} from "src/app/_services";
import {first} from "rxjs";
import {NgClass, NgIf} from "@angular/common";
import {User} from "@app/_models";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    imports: [
        ReactiveFormsModule,
        NgClass,
        NgIf,
        RouterLink
    ],
    standalone: true
})
export class LoginComponent implements OnInit {
    form!: FormGroup;
    loading: boolean = false;
    submitted: boolean = false;

    constructor(private formBuilder: FormBuilder,
                private router: Router,
                private auth: AuthService,
                private alertService: AlertService) {
        if (this.auth.userValue) {
            this.router.navigate(["/"]);
        }
    }

    ngOnInit(): void {
        this.form = this.formBuilder.group({
            username: ["", [Validators.required, Validators.minLength(6)]],
            password: ["", [Validators.required, Validators.minLength(6)]]
        });
    }

    onSubmit(): void {
        this.submitted = true;
        this.alertService.clear();
        if (this.form.invalid) {
            return;
        }

        const user: User = {
            username: this.form.controls['username'].value,
            password: this.form.controls['password'].value
        }

        this.loading = true;
        this.auth.login(user)
            .pipe(first())
            .subscribe({
                next: () => {
                    this.router.navigate(["/account/register"]);
                },
                error: error => {
                    this.alertService.error(error.message);
                    this.loading = false;
                }
            })
    }

}
