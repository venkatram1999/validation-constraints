# Member GraphQL API

This project is a simple Spring Boot GraphQL application for managing members. It demonstrates how to use a custom `Email` scalar, validate and sanitize input data, and return clean GraphQL errors from resolver methods [web:68][web:293][web:297].

## Schema

```graphql
scalar Email

type Member {
  id: ID!
  name: String!
  email: Email!
}

input AddMemberInput {
  name: String!
  email: Email!
}

type Query {
  members: [Member!]!
  memberByEmail(email: Email!): Member
}

type Mutation {
  addMember(input: AddMemberInput!): Member
}
```

## Custom scalars

A custom scalar is used when a normal GraphQL scalar such as `String`, `Int`, or `Boolean` is not enough. In this project, `Email` is defined as a custom scalar so the API can validate email values at the GraphQL layer itself instead of treating them as plain strings.

The `Email` scalar is implemented with GraphQL Java `Coercing`. The `parseValue(...)` method validates values sent as variables, `parseLiteral(...)` validates inline values written directly in the GraphQL request, and `serialize(...)` validates values returned in the response.

This approach gives two advantages: the schema becomes more expressive, and invalid email input is rejected before the mutation or query logic starts running.

### Example scalar usage

```graphql
mutation {
  addMember(input: {
    name: "Venkat Ramana"
    email: "venkat@gmail.com"
  }) {
    id
    name
    email
  }
}
```

## Input validation and sanitization

Input validation checks whether incoming values satisfy required rules. In this project, the `AddMemberInput` class uses Jakarta Bean Validation annotations such as `@NotBlank` and `@Size`, and validation is triggered in the resolver method using `@Valid` on `@Argument`.

Validation ensures that the request is structurally correct, but sanitization makes the input clean and consistent before saving it. In the service layer, the name is trimmed and multiple spaces are reduced to a single space, while the email is trimmed and converted to lowercase so duplicate comparisons work reliably.

### Name sanitization example

```java
private String sanitizeName(String name) {
    return name == null ? null : name.trim().replaceAll("\\s+", " ");
}
```

Example:

```text
"   Venkat    Ramana   " -> "Venkat Ramana"
```

### Email sanitization example

```java
private String sanitizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
}
```

Example:

```text
"  VENKAT@GMAIL.COM  " -> "venkat@gmail.com"
```

### Business constraints

Besides DTO validation and scalar validation, the service layer also checks business rules. In this project, one important rule is that the same email cannot be added more than once.

## Error handling in resolvers

In Spring for GraphQL, annotated controller methods such as `@QueryMapping` and `@MutationMapping` act as resolvers. When an exception is thrown from a resolver or service, `@GraphQlExceptionHandler` can convert that exception into a structured GraphQL error response instead of exposing raw server details.

This project can use exception handling for cases such as invalid input, member not found, duplicate email, or unexpected internal errors. Centralized GraphQL exception handling keeps the resolver code cleaner and gives the client predictable error messages.

## Example requests

### Add member mutation

```graphql
mutation {
  addMember(input: {
    name: "  Venkat Ramana  "
    email: "  VENKAT@GMAIL.COM "
  }) {
    id
    name
    email
  }
}
```

Expected behavior:
- `name` is sanitized to `Venkat Ramana`
- `email` is sanitized to `venkat@gmail.com`
- The member is created only if validation passes and the email is unique

### Get all members

```graphql
query {
  members {
    id
    name
    email
  }
}
```

### Get member by email

```graphql
query {
  memberByEmail(email: "sita@gmail.com") {
    id
    name
    email
  }
}
```

## Request body example

When testing in Postman or another HTTP client, the GraphQL request body is usually sent as JSON with a `query` field.

### Mutation request body

```json
{
  "query": "mutation { addMember(input: { name: \"  Venkat Ramana  \", email: \"  VENKAT@GMAIL.COM \" }) { id name email } }"
}
```

### Query request body

```json
{
  "query": "query { members { id name email } }"
}
```

## Validation summary

This project validates data at multiple levels:

- GraphQL schema validation: required fields such as `String!` and `Email!` must be present
- Custom scalar validation: the `Email` scalar checks format at GraphQL parsing time 
- DTO validation: `@NotBlank`, `@Size`, and `@Valid` validate input object fields 
- Service validation: duplicate email and other business rules are checked before saving
- Sanitization: input is cleaned before persistence for consistency 

## Why this design is useful

This structure keeps responsibilities clear. The schema defines API shape, the scalar validates special values, the DTO validates input structure, the service applies business constraints and sanitization, and the resolver exception handler returns clean GraphQL errors to clients.
