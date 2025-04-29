Backend Core Components

Entities: Create JPA entities such as Player, Team, and Game in the model package.

Repositories: Define interfaces extending JpaRepository in the repository package for data access

Services: Implement business logic in the service package, interacting with repositories.

Controllers: Expose RESTful endpoints in the controller package using @RestController.

DTOs: Use Data Transfer Objects in the dto package to encapsulate and transfer data between layers.