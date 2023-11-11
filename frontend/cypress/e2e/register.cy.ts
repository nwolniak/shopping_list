/// <reference types="cypress" />

describe("Register page", () => {
    beforeEach(() => {
        cy.visit("http://localhost:4200/account/register")
    })

    it("displays register form", () => {
        cy.get(".card").should("exist")
        cy.get(".card-header").should("contain.text", "Register")
        cy.get(".card-body").should("exist")
        cy.get(".username-label").should("contain.text", "Username")
        cy.get(".password-label").should("contain.text", "Password")
        cy.get(".register-button").should("contain.text", "Register")
        cy.get(".cancel-link").should("contain.text", "Cancel")
    })

    it("can register", () => {
        cy.get(".username-input").type("username")
        cy.get(".password-input").type("password")
        cy.get(".register-button").click()
        cy.wait(1000)
    })

    it("can go back from registration page", () => {
        cy.get(".cancel-link").click()
        cy.url().should("equal", "http://localhost:4200/account/login")
    })

})
