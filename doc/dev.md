# mvn commands

## to add NOTICE
`./mvnw notice:check` Checks that a NOTICE file exists and that its content match what would be generated.
`./mvnw notice:generate` Generates a new NOTICE file, replacing any existing NOTICE file.

## to add licence headers
`./mvnw license:check` verify if some files miss license header
`./mvnw license:format` add the license header when missing. If a header is existing, it is updated to the new one.
`./mvnw license:remove` remove existing license header

## to see deprecated code and warnings
`./mvnw compile -Dmaven.compiler.showWarnings=true -Dmaven.compiler.showDeprecation=true`

## to update maven wrapper

see official doc: https://maven.apache.org/wrapper/

## to run

`./mvnw clean compile spring-boot:run -Dspring.profiles.active=dev -Dspring-boot.run.fork=false -Dspring.config.additional-location=$PATH_PROPERTIES/RessourcesDiffusables/application-local.yml`

## To see deprecated code and warnings
`./mvnw compile -Dmaven.compiler.showWarnings=true -Dmaven.compiler.showDeprecation=true`

## To update maven wrapper
see official doc: https://maven.apache.org/wrapper/
