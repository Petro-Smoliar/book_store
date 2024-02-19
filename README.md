# Bookstore Application

## Introduction

The "Bookstore" project is a comprehensive application designed for managing and operating an online bookstore. It
provides an extensive range of functionalities, including user authentication, handling shopping carts, managing book
categories, processing orders, and more. The application is built using Spring Boot, Spring Security, and utilizes JSON
Web Tokens (JWT) for secure user authentication.

## Technologies Used

+ ***Spring Boot*** : Framework for building Java-based applications.
+ ***Spring Security*** : Provides security features for the application.
+ ***JWT (JSON Web Tokens)*** : Used for secure user authentication.
+ ***Swagger***: Enables API documentation.
+ ***Jakarta Validation*** : Ensures data integrity through validation annotations.
+ ***Lombok*** : Reduces boilerplate code by providing annotations for common tasks.

## Controllers

### AuthenticationController

#### Endpoints:

+ [/api/auth/registration][registgration] : Register a new user.
+ [/api/auth/login][login] : Authenticate and obtain a JWT token.

### BookController

#### Endpoints:

+ [/api/books][get_book] : Get all books.
+ [/api/books/{id}][get_book_by_id] : Get a book by ID.
+ [/api/books/search][book_searc] : Search for books based on specified parameters.
+ [/api/books (POST)][save_book] : Create a new book (admin only).
+ [/api/books/{id} (PUT)][book_update] : Update a book by ID (admin only).
+ [/api/books/{id} (DELETE)][book_delete] : Delete a book by ID (admin only).

### CategoryController

#### Endpoints:

+ [/api/categories][get_all_category] : Get all categories.
+ [/api/categories/{id}][get_category_by_id] : Get a category by ID.
+ [/api/categories (POST)][save_category] : Create a new category (admin only).
+ [/api/categories/{id} (PUT)][update_category] : Update a category by ID (admin only).
+ [/api/categories/{id} (DELETE)][delete_category] : Delete a category by ID (admin only).
+ [/api/{id}/books][get_book_by_category] : Get books by a specific category.

### OrderController

#### Endpoints:

+ [/api/orders (POST)][add_order] : Place a new order.
+ [/api/orders (GET)][get_all_orders] : Get all orders.
+ [/api/orders/{id} (PATCH)][update_status] : Update order status (admin only).
+ [/api/orders/{orderId}/items (GET)][get_items_by_order] : Get order items.

### ShoppingCartController

#### Endpoints:

+ [/api/cart (GET)][get_shopping_cart] : Retrieve the shopping cart for the authenticated user.
+ [/api/cart (POST)][add_cart] : Add a new item to the shopping cart.
+ [/api/cart/cart-items/{cartItemId} (PUT)][update_cart_item] : Update a shopping cart item.
+ [/api/cart/cart-items/{cartItemId} (DELETE)][delete_cart_item] : Delete a shopping cart item.

## Security Configuration

The security configuration ensures that specific endpoints are accessible only to authenticated users or users with
admin roles. JWT tokens are used for authentication.

## Prerequisites

Before running the application, ensure you have the following prerequisites installed:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## How to Run with Docker

1. Clone the repository:
   ```bash
   git clone https://github.com/Petro-Smoliar/book_store.git
2. Navigate to the project root directory:
   ```bash 
   cd your/repository
3. Build the Maven project:
   ```bash 
   mvn clean package
4. Build the Docker image:
   ```bash
   docker-compose build .
5. Run the Docker container:
   ```bash
   docker-compose up

## Challenges and Solutions

Validation Errors: Addressed potential issues with invalid input data by using ***@Valid*** annotations in controllers
for input validation.

## Demo Video

Check out the [demo video][demo video] to see the Bookstore application in action.


[add_cart]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/add_cart_item.jpg

[registgration]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/registration.jpg

[login]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/login.jpg

[get_book]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/get_book.jpg

[get_book_by_id]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/get_book_by_id.jpg

[book_searc]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/book_search.jpg

[save_book]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/save_book.jpg

[book_update]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/book_update.jpg

[book_delete]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/book_delete.jpg

[get_all_category]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/get_all_category.jpg

[get_category_by_id]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/get_by_id_category.jpg

[save_category]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/save_category.jpg

[update_category]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/update_category.jpg

[delete_category]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/delete_category.jpg

[get_book_by_category]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/get_book_by_category.jpg

[add_order]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/add_order.jpg

[get_all_orders]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/get_all_orders.jpg

[update_status]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/update_status_order.jpg

[get_items_by_order]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/get_items_by_order.jpg

[get_shopping_cart]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/get_shopping_cart.jpg

[update_cart_item]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/update_cart_item.jpg

[delete_cart_item]: https://github.com/Petro-Smoliar/book_store/blob/main/screenshots/delete_cart%20item.jpg

[demo video]: https://www.loom.com/share/2e0fe6660012471a869e1d99b371b814?sid=a9175f49-a3fd-42b8-a6b6-90f1e221bcdc