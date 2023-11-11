import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {AlertService, ListService} from "@app/_services";
import {first} from "rxjs";
import {NgClass, NgIf} from "@angular/common";
import {Item} from "@app/_models";

@Component({
    templateUrl: "add.component.html",
    imports: [
        ReactiveFormsModule,
        NgClass,
        RouterLink,
        NgIf
    ],
    standalone: true
})
export class AddComponent implements OnInit {
    form!: FormGroup;
    listId?: number;
    submitting: boolean = false;
    submitted: boolean = false;

    constructor(private formBuilder: FormBuilder,
                private route: ActivatedRoute,
                private router: Router,
                private listService: ListService,
                private alertService: AlertService
    ) {
    }

    ngOnInit(): void {
        this.listId = parseInt(this.route.snapshot.params['listId']);
        this.form = this.formBuilder.group({
            name: ['', [Validators.required, Validators.pattern("[a-zA-Z]+")]],
            units: ['', [Validators.required, Validators.pattern("[0-9]+")]],
            unitType: ['', Validators.required]
        });
    }

    onSubmit() {
        this.submitted = true;
        this.alertService.clear();
        if (this.form.invalid || !this.listId) {
            return;
        }

        let item: Item = new Item(
            this.form.value.name,
            parseInt(this.form.value.units),
            this.form.value.unitType
        );
        this.submitting = true;
        this.listService.addItemToShoppingList(this.listId, item)
            .pipe(first())
            .subscribe({
                next: () => {
                    this.alertService.success("Item added", true);
                    this.router.navigate(["/shopping-lists"]);
                },
                error: error => {
                    this.alertService.error(error.message);
                    this.submitting = false;
                }
            })
    }

}
