# Customer

**Aggregate Root:** `Customer`

**Identity:** `CustomerId` (UUID)

## Attributes

| Field         | Type              | Constraints               |
|---------------|-------------------|---------------------------|
| `id`          | `UUID`            | PK                        |
| `firstName`   | `String`          | `@NotBlank`               |
| `lastName`    | `String`          | `@NotBlank`               |
| `dateOfBirth` | `LocalDate`       | `@NotNull`                |
| `clubMember`  | `boolean`         | —                         |
| `email`       | `String`          | `@NotBlank`               |
| `phone`       | `String`          | optional                  |
| `address`     | `CustomerAddress` | `@NotNull`, `@Embeddable` |
| `createdAt`   | `Instant`         | `@NotNull`                |
| `updatedAt`   | `Instant`         | `@Version`                |

**Value Object:** `CustomerAddress` (embedded)

- `addressLine1` (`@NotBlank`), `addressLine2` (optional), `postalCode` (`@NotBlank`), `city` (`@NotBlank`), `country` (
  `@NotBlank`)

## Commands

| Command                        | Fields                                                                                                            |
|--------------------------------|-------------------------------------------------------------------------------------------------------------------|
| `CreateCustomerCommand`        | firstName, lastName, dateOfBirth, clubMember, email, phone, addressLine1, addressLine2, postalCode, city, country |
| `UpdateCustomerCommand`        | id (`CustomerId`), firstName, lastName, dateOfBirth, clubMember                                                   |
| `UpdateCustomerContactCommand` | id (`CustomerId`), email, phone                                                                                   |
| `UpdateCustomerAddressCommand` | id (`CustomerId`), addressLine1, addressLine2, postalCode, city, country                                          |

## Business Rules / Invariants

| #   | Rule                                                    | Enforced in                                            |
|-----|---------------------------------------------------------|--------------------------------------------------------|
| C-1 | `firstName`, `lastName`, `email` must not be blank      | Bean Validation on Entity + Commands                   |
| C-2 | `dateOfBirth` must not be null                          | Bean Validation                                        |
| C-3 | Address must always be present and valid                | `@NotNull` on embedded `CustomerAddress`               |
| C-4 | Customer must not be deleted if assigned to an RFID tag | **TODO** — noted in `CustomerService.deleteCustomer()` |

## Domain Events

| Event                      | Payload                |
|----------------------------|------------------------|
| `CUSTOMER_CREATED`         | Full customer snapshot |
| `CUSTOMER_UPDATED`         | Updated base fields    |
| `CUSTOMER_CONTACT_CHANGED` | email, phone           |
| `CUSTOMER_ADDRESS_CHANGED` | Full address           |
| `CUSTOMER_DELETED`         | CustomerId             |
