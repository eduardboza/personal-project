# Top level architecture

## Overview

The solution is composed of 2 main services:

* Order Service
* Fulfillment Service

# Implementation

## Order Service

The service will contain all the CRUD operations for the Order entity. It will also contain the business logic for the
Order entity.

## Tech Stack

* Java 21
* Sprint Boot 3
* Spring Data JPA (Hibernate optionals)
    * Envers (Audit)
    * QueryDSL
    * Hibernate Search
* REST API
* JUnit 5
    * Integration tests (REST API, Repositories and Services)
    * Unit tests (JPA Entities + Logic, and Services)
* postgres
* flyway -> DB migrations
* Testing with postman (manual testing)

## Implementation steps

* Setup Spring Boot Backend
* Setup DB migration using flyway
* Setup JPA Entities
* Setup Repositories
    * persist
    * update
    * delete
* Setup REST API
  

