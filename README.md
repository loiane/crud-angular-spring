# REST API with Spring Boot and Angular

CRUD Angular + Spring demonstrating Has-Many relationship, with tests.

## 💻 Tecnologies

- Java 17
- Spring Boot 3 (Spring 6)
- JPA + Hibernate
- JUnit 5 + Mockito (back-end tests)
- Maven
- Angular v15
- Angular Material
- Karma + Jasmine (front-end tests)

## ⌨️ Editor / IDE

- Visual Studio Code
- Java Extensions [link](https://marketplace.visualstudio.com/items?itemName=loiane.java-spring-extension-pack)
- Angular Extensions [link](https://marketplace.visualstudio.com/items?itemName=loiane.angular-extension-pack)

## Some functionalies available in the API

- ✅ Java model class with validation
- ✅ JPA repository
- ✅ JPA Pagination
- ✅ Controller, Service and Repository layers
- ✅ Has-Many relationship (Course-Lessons)
- ✅ Java 17 Records as DTO (Data Transfer Object)
- ✅ Hibernate / Jakarta Validation
- ✅ Unit tests for all layers (repository, service, controller)
- ✅ Test coverage for tests
- ✅ Spring Docs - Swagger (https://springdoc.org/v2/)

## Some functionalies available in the Front-end

- [ ] Angular Material components
- [ ] List of all courses
- [ ] Form to update/create courses with lessons (has-many - FormArray)
- [ ] Form asyn validation for duplicated courses and lessons
- [ ] View only screen
- [ ] TypedForms (Angular v14+)
- [ ] Presentational x Smart Components
- [ ] Unit and Integration tests for components, services, pipes, guards

## ❗️Excuting the code locally

### Executing the back-end

You need to have Java and Maven installed and configured locally.

Open the `crud-spring` project in your favorite IDE as a Maven project and execute it as Spring Boot application.

### Executing the front-end

You need to have Node.js / NPM installed locally.

1) Install all the required depencencies:

```
npm install
```

2) Execute the project:

```
npm run start
```

This command will run the Angular project with a proxy to the Java server, without requiring CORS.

Open your browser and access **http://localhost:4200** (Angular default port).
