IMAGE_NAME = vkbot
CONTAINER_NAME = vkbot-container
PORT = 8080

JAR_FILE = build/libs/VkBot-0.0.1-SNAPSHOT.jar

GRADLE = ./gradlew

build-gradle:
	$(GRADLE) build

build-docker: build-gradle
	docker build -t $(IMAGE_NAME) .

stop:
	docker stop $(CONTAINER_NAME) || true

up: 
	TOKEN=$(TOKEN) CONFIRM_CODE=$(CONFIRM_CODE) docker compose up -d

down:
	docker compose down

build: build-gradle build-docker

test:
	./gradlew test
