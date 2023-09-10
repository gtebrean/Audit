Audit: DiffTool
===============

This repository represents core of an audit system that is capable of determining the difference between two objects of
the same type, according to the proposed codding challenge.

Implementation
--------------

The main entry point is represented by the `DiffTool` class, which houses the `diff(Object, Object)` method. This method
is designed to accept two objects as input parameters and is capable of returning a list of `ChangeType` objects. This
list contains all detected differences between the provided objects.

To enable the discovery of differences within nested fields, the difference-finding approach is required to support
queries into these nested objects fields. To avoid using reflection within multiple Java-specific objects (e.g.,
String.class, BigDecimal.class, etc.), a field will be considered as a nested item if its type is annotated with
`@NestedItem`.

A series of unit tests were implemented to ensure that the functionalities meet the expected outputs, following a
Test-Driven Development (TDD) approach. These tests are situated in the `test.java.com.audit` package. To mimic real
scenarios, a set of entities were constructed within the test package to serve as inputs.

Execute tests by running:

``` {.sourceCode .bash}
$ ./gradlew test
```

Usage
------
In order to use it clone the repository and choose one of the following options:

A. Open the project in IntelliJ IDEA and proceed to expand it by incorporating additional business logic as needed.

B. Co to project folder and run the command bellow. It will release a the `Audit-1.0-SNAPSHOT.jar` to the maven local
repository which can be added as dependency to other projects (gradle
e.g.: `implementation 'com.audit:Audit:1.0-SNAPSHOT'`)

``` {.sourceCode .bash}
$ gradle :publishToMavenLocal
```

Tools
--------------

Java 17 Lombok Spotless Junit 5

Implementation time `6h`