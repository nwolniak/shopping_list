/// <reference types="cypress" />

describe("Login page", () => {
    beforeEach(() => {
        cy.visit("http://localhost:4200/account/login")
    })

    it("displays login form", () => {
        cy.get(".card").should("exist")
        cy.get(".card-header").should("contain.text", "Login")
        cy.get(".card-body").should("exist")
        cy.get(".username-label").should("contain.text", "Username")
        cy.get(".password-label").should("contain.text", "Password")
        cy.get(".login-button").should("contain.text", "Login")
        cy.get(".register-link").should("contain.text", "Register")
    })

    it("can login", () => {
        cy.get(".username-input").type("username")
        cy.get(".password-input").type("password")
        cy.get(".login-button").click()
        cy.url().should("equal", "http://localhost:4200/")
    })

    it("can navigate to registration page", () => {
        cy.get(".register-link").click()
        cy.url().should("equal", "http://localhost:4200/account/register")
    })

})
