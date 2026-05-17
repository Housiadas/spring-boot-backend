lint:
	docker run --rm -v "$(PWD)":/app -w /app eclipse-temurin:26-jdk ./gradlew spotlessApply

test:
