import {AlertService} from "@app/_services";
import {of} from "rxjs";
import {fakeAsync, TestBed, tick} from "@angular/core/testing";
import {ReactiveFormsModule} from "@angular/forms";
import {AlertComponent} from "@app/components/alert.component";

describe("Alert Component", () => {
    let component: AlertComponent;
    let alertService: AlertService;

    const alertServiceMock = {
        onAlert() {
        },
        clear() {
        },
        success() {
        },
        error() {
        }
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [ReactiveFormsModule],
            providers: [
                AlertComponent,
                {provide: AlertService, useValue: alertServiceMock},
            ],
        });
        component = TestBed.inject(AlertComponent);
        alertService = TestBed.inject(AlertService);
    });

    it("should handle success alert event", () => {
        spyOn(alertService, "onAlert").and.returnValue(of({type: "success", msg: "Success"}))

        component.ngOnInit();

        expect(component.alert).toBeDefined();
        expect(component.alert.cssClass).toEqual("alert-success");
    });

    it("should handle error alert event", () => {
        spyOn(alertService, "onAlert").and.returnValue(of({type: "error", msg: "Error"}))

        component.ngOnInit();

        expect(component.alert).toBeDefined();
        expect(component.alert.cssClass).toEqual("alert-danger");
    });

    it("should run alertService.clear() method when clear", () => {
        spyOn(alertService, "onAlert").and.returnValue(of({type: "error", msg: "Success"}))
        spyOn(alertService, "clear");
        component.ngOnInit();
        component.clear();
        expect(alertService.clear).toHaveBeenCalled();
    });

    it("should run clear() method after timeout", fakeAsync(() => {
        spyOn(alertService, "onAlert").and.returnValue(of({type: "success", msg: "Success"}))
        spyOn(component, "clear");

        component.ngOnInit();

        tick(2500);

        expect(component.clear).toHaveBeenCalled();
    }));

});
