DB_URL ?= jdbc:postgresql://localhost:5432/mydatabase
DB_USER ?= myuser
DB_PASSWORD ?= secret

FLYWAY = docker run --rm --network=host \
	-v "$(PWD)/src/main/resources/db/migration":/flyway/sql \
	flyway/flyway:latest \
	-url=$(DB_URL) -user=$(DB_USER) -password=$(DB_PASSWORD)

lint:
	docker run --rm -v "$(PWD)":/app -w /app eclipse-temurin:26-jdk ./gradlew spotlessApply

test:
	docker run --rm -v "$(PWD)":/app -w /app eclipse-temurin:26-jdk ./gradlew test

migrate/up:
	$(FLYWAY) migrate

migrate/down:
	$(FLYWAY) -cleanDisabled=false clean
