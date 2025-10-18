## General Rules
1. We use DDD Layered Architecture to implement our backend code
1.1 There are 4 layers: domain, application, infrastructure, and API layers
1.2 The domain and application layers should be pure and decoupled with 3rd party libraries.
2. We should use Hexagon Architecture to decouple the interface and implementation to 3rd party services
2.1 Ports are defined in the domain layer
2.2 Adapters are implemented in the infrastructure layer.
Mock adapters are created by default so that we can use them in the integration service. Configs are created to turn mock adapters on or off.

## Domain Layer Rules
1. IDs should be implemented as Value Object to decouple from the database ID.
For example, a student ID with an integer database type should have value object class like StudentId and a factor method to create it from integer.

2. All aggregates should be create from factory method instead of constructor and entities should be created by its owner aggregate.

3. Don't create property set methods and use method with meaningful business name.
For example, don't create a setInUse method and create a enable/disable methods to tighten the aggregate operations.

## Application Layer
1. Application layer should be thin and without business logic.
2. For queries, use command and query pattern for implementation.
Query interfaces are defined in the application layer and it is usually implemented in infrastructure layer.

## Infrastructure Layer
1. Ports are implemented with adapters under adapter folder.
1.1 Create a mock for each port so that we can use mocks for integration test when some 3rd party services are not available.
2. Repositories are implemented under repository folder
