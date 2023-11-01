import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {AlertService, AuthService} from "src/app/_services";
import {first} from "rxjs";
import {NgClass, NgIf} from "@angular/common";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  imports: [
    ReactiveFormsModule,
    NgClass,
    NgIf,
    RouterLink,
    FontAwesomeModule
  ],
  standalone: true
})
export class RegisterComponent implements OnInit {
  form!: FormGroup;
  loading: boolean = false;
  submitted: boolean = false;


  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router,
              private auth: AuthService,
              private alertServiec: AlertService) {
    if (this.auth.userValue) {
      this.router.navigate(["/"]);
    }
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      username: ["", Validators.required],
      password: ["", [Validators.required, Validators.minLength(6)]]
    })
  }

  onSubmit() {
    this.submitted = true;
    this.alertServiec.clear();
    if (this.form.invalid) {
      return;
    }

    this.loading = true;
    this.auth.register(this.form.value)
      .pipe(first())
      .subscribe({
        next: () => {
          this.router.navigate(["/login"], {queryParams: {registered: true}})
        },
        error: error => {
          this.alertServiec.error(error);
          this.loading = false;
        }
      })
  }

}
