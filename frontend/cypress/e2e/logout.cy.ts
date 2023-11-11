/// <reference types="cypress" />

describe("Main page", () => {
    beforeEach(() => {
        cy.visit("http://localhost:4200/account/login")
        cy.get(".username-input").type("username")
        cy.get(".password-input").type("password")
        cy.get(".login-button").click()
        cy.url().should("equal", "http://localhost:4200/")
    })

    it("should allow to logout user", () => {
        cy.get(".dropdown-logout").should("exist")
        cy.get(".dropdown-logout").click()
        cy.get(".logout-button").should("contain.text", "Logout")
        cy.get(".logout-button").click()
        cy.url().should("equal", "http://localhost:4200/account/login")
    })
})
