/// <reference types="cypress" />

describe("Shopping Lists page", () => {
    beforeEach(() => {
        cy.visit("http://localhost:4200/account/login")
        cy.get(".username-input").type("username")
        cy.get(".password-input").type("password")
        cy.get(".login-button").click()
        cy.url().should("equal", "http://localhost:4200/")
    })

    it("displays empty shopping list view", () => {
        cy.get("h1").should("contain", "Shopping Lists")
        cy.get(".add-list-button").should("contain.text", "Add List")
        cy.get(".card").should("not.exist")
    })

    it("should allow to add shopping list", () => {
        cy.get(".add-list-button").click()
        cy.get(".card").should("exist")
        cy.get(".card").should("have.length", 1)
    })

    it("should allow to add item to shopping list", () => {
        cy.get(".add-item-link").click()
        cy.url().should("contain", "add-item")
        cy.get(".name-input").type("apple")
        cy.get(".units-input").type("2")
        cy.get(".unit-type-input").select("Kg")
        cy.get(".submit-button").click()
        cy.url().should("equal", "http://localhost:4200/")
        cy.get(".item").should("have.length", 1)
        cy.get(".item").should("contain.text", "apple")
        cy.get(".item").should("contain.text", "2")
        cy.get(".item").should("contain.text", "Kg")
    })

    it("should allow to add next item to shopping list", () => {
        cy.get(".add-item-link").click()
        cy.url().should("contain", "add-item")
        cy.get(".name-input").type("pear")
        cy.get(".units-input").type("2")
        cy.get(".unit-type-input").select("Szt")
        cy.get(".submit-button").click()
        cy.url().should("equal", "http://localhost:4200/")
        cy.get(".item").should("have.length", 2)
        cy.get(".item").should("contain.text", "pear")
        cy.get(".item").should("contain.text", "2")
        cy.get(".item").should("contain.text", "Szt")
    })

    it("should allow to realize item", () => {
        cy.get(".realize-button").first().click()
        cy.get(".item").first().should("have.class", "table-success")
        cy.get(".item").first().should("not.contain.html", "button")
    })

    it("should allow to delete item", () => {
        cy.get(".delete-item-button").should("have.length", 1)
        cy.get(".delete-item-button").click()
        cy.get(".item").should("have.length", 1)
    })

    it("should allow to choose shopping list purchase date", () => {
        cy.get("mat-datepicker-toggle").click();
        cy.get("button[aria-label='February 29, 2024']").click();
        cy.get(".date-input").should("have.attr", "placeholder", "Purchase Date: Feb 29, 2024");
    })

    it("should allow to change current shopping list purchase date", () => {
        cy.get(".date-input").should("have.attr", "placeholder", "Purchase Date: Feb 29, 2024");
        cy.get("mat-datepicker-toggle").click();
        cy.get("button[aria-label='February 29, 2024']").click();
        cy.get(".date-input").should("have.attr", "placeholder", "Purchase Date: Feb 29, 2024");
    })

    it("should allow to delete shopping lists", () => {
        cy.get(".delete-button").each(($button) => {
            cy.wrap($button).click();
        })
        cy.get(".card").should("not.exist")
    })

})
