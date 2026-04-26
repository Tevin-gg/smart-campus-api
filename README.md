# Smart Campus Sensor & Room Management API

Module: 5COSC022W — Client Server Architectures  
Student Name: Tevin Bandara  
Student ID: 20231725 
Video Demonstration Link: [ADD YOUR VIDEO LINK]

## Overview

This project provides the REST API for the Smart Campus coursework. The system is designed to keep track of campus rooms manage the sensors placed inside them, and record all the data those sensors collect over time. Its built with Java and Jersey and runs on a standalone Grizzly server so you don't need external web containers like Tomcat. Because the coursework specifically asked us to avoid external database engines, the entire application runs in memory. I set up a central Database class that uses standard Java collections to store and retrieve all the records while the server is alive.

The project demonstrates:

- RESTful API design
- JAX-RS resource classes
- Room and sensor management
- Sensor filtering using query parameters
- Nested sub-resources for sensor readings
- Custom exception handling using exception mappers
- Request and response logging using JAX-RS filters

## Technology Stack

- Java 11 or higher
- Maven
- JAX-RS / Jersey
- Embedded Grizzly HTTP Server
- Jackson for JSON support
- Java in-memory collections

This project does not use Spring Boot or any database technology.

## Base URL

```text
http://localhost:8080/api/v1
```

## API Design Summary

The API is based on three main models:

### Room

A room represents a physical room in the campus.

Main fields:

- `id`
- `name`
- `capacity`
- `sensorIds`

### Sensor

A sensor represents a hardware device installed inside a room.

Main fields:

- `id`
- `type`
- `status`
- `currentValue`
- `roomId`

### SensorReading

A sensor reading represents one recorded measurement from a sensor.

Main fields:

- `id`
- `timestamp`
- `value`

The relationship between the models is:

```text
Room -> Sensor -> SensorReading
```

A room can have many sensors.  
A sensor belongs to one room.  
A sensor can have many readings.

Sensor readings are implemented as a nested sub-resource:

```text
/api/v1/sensors/{sensorId}/readings
```

## Build and Run

### Requirements

- Java JDK 11 or higher
- Apache Maven 3.6 or higher

Check installed versions:

```bash
java -version
mvn -version
```

### Step 1 — Clone the Repository

```bash
git clone https://github.com/tevinbandara/smart-campus-api
cd smart-campus-api
```

### Step 2 — Build the Project

Run this command from the project root folder:

```bash
mvn clean package
```

Wait until Maven shows - `BUILD SUCCESS`

### Step 3 — Run the Server

Run the jar file:

```bash
java -jar target/smart-campus-api-1.0.0.jar
```

On my Mac setup, I used the full Homebrew Java path:

```bash
/opt/homebrew/opt/openjdk/bin/java -jar target/smart-campus-api-1.0.0.jar
```

The server starts on port `8080`.

### Step 4 — Verify the API

Open this URL in a browser, Postman - 

```text
http://localhost:8080/api/v1
```

The API should return the discovery JSON response.

## Preloaded Sample Data

The application starts with sample in-memory data so the API can be tested immediately after startup.

### Rooms

- `LAB-101` — Computer Lab 101
- `LIB-201` — Library Study Area

### Sensors

- `CO2-001` — CO2 sensor in `LAB-101`
- `TEMP-001` — Temperature sensor in `LIB-201`

Each preloaded sensor also has an initial reading history.

## Main Endpoints
## Main Endpoints

The API starts from the discovery endpoint at `GET /api/v1`. This returns the basic API information and links to the main resources.

Room related requests are handled under `/api/v1/rooms`. A `GET` request to this path returns all rooms, and a `POST` request creates a new room. A single room can be accessed using `/api/v1/rooms/{roomId}`. This path supports `GET` to view one room and `DELETE` to remove a room if it has no sensors assigned.

Sensor related requests are handled under `/api/v1/sensors`. A `GET` request returns all sensors, while `GET /api/v1/sensors?type=CO2` filters sensors by type. A `POST` request to `/api/v1/sensors` creates a new sensor, but the sensor must be linked to an existing room.

Sensor readings are handled as a nested resource under a sensor. The path is `/api/v1/sensors/{sensorId}/readings`. A `GET` request returns the reading history for that sensor, and a `POST` request adds a new reading. When a new reading is added, the parent sensor’s `currentValue` is also updated.

## The Curl Commands

All commands assume the server is running on `http://localhost:8080`

### 1. Discovery Endpoint

```bash
curl -i http://localhost:8080/api/v1
```

### 2. Get All Rooms

```bash
curl -i http://localhost:8080/api/v1/rooms
```

### 3. Create a New Room

```bash
curl -i -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"ENG-301","name":"Engineering Lab","capacity":35}'
```

### 4. Get a Specific Room

```bash
curl -i http://localhost:8080/api/v1/rooms/ENG-301
```

### 5. Delete a Room That Still Has Sensors

```bash
curl -i -X DELETE http://localhost:8080/api/v1/rooms/LAB-101
```

Expected result - `409 Conflict`

### 6. Create and Delete an Empty Room

Create the room - 

```bash
curl -i -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"BUS-101","name":"Business Room","capacity":20}'
```

Delete the room - 

```bash
curl -i -X DELETE http://localhost:8080/api/v1/rooms/BUS-101
```

Expected result - `200 OK`

### 7. Get All Sensors

```bash
curl -i http://localhost:8080/api/v1/sensors
```

### 8. Filter Sensors by Type

```bash
curl -i "http://localhost:8080/api/v1/sensors?type=CO2"
```

### 9. Create a Valid Sensor

```bash
curl -i -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"OCC-001","type":"Occupancy","status":"ACTIVE","currentValue":12.0,"roomId":"LAB-101"}'
```

### 10. Create a Sensor with an Invalid Room ID

```bash
curl -i -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-999","type":"CO2","status":"ACTIVE","currentValue":500.0,"roomId":"NO-ROOM"}'
```

Expected result - `422 Unprocessable Entity`

### 11. Get Sensor Reading History

```bash
curl -i http://localhost:8080/api/v1/sensors/CO2-001/readings
```

### 12. Add a New Sensor Reading

```bash
curl -i -X POST http://localhost:8080/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":510.7}'
```

After this request, the parent sensor’s `currentValue` is also updated.

### 13. Check Updated Parent Sensor Value

```bash
curl -i "http://localhost:8080/api/v1/sensors?type=CO2"
```

The `currentValue` of `CO2-001` should now show the latest reading value.

### 14. Create a Maintenance Sensor

```bash
curl -i -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-900","type":"Temperature","status":"MAINTENANCE","currentValue":0.0,"roomId":"LAB-101"}'
```

### 15. Try Adding a Reading to a Maintenance Sensor

```bash
curl -i -X POST http://localhost:8080/api/v1/sensors/TEMP-900/readings \
  -H "Content-Type: application/json" \
  -d '{"value":29.5}'
```

Expected result - `403 Forbidden`

### 16. Test an Unknown API Path

```bash
curl -i http://localhost:8080/api/v1/wrong
```

Expected result - `404 Not Found`

### 17. Test Wrong Content-Type

```bash
curl -i -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: text/plain" \
  -d 'hello'
```

Expected result - `415 Unsupported Media Type`

## Error Handling

I set up custom exception mappers so the API handles errors professionally. If something goes wrong on the server, the system intercepts the crash. Instead of passing messy, raw Java errors back to the user, the API automatically formats the problem into a simple JSON response with the right HTTP status code. This keeps the backend logic hidden and ensures that whoever is using the API always gets a clear, consistent error message they can actually understand.

If a client tries to delete a room that still has sensors assigned, the API uses `RoomNotEmptyException` and returns `409 Conflict`.

If a client tries to create a sensor using a `roomId` that does not exist, the API uses `LinkedResourceNotFoundException` and returns `422 Unprocessable Entity`.

If a client tries to add a new reading to a sensor that is in `MAINTENANCE`, the API uses `SensorUnavailableException` and returns `403 Forbidden`.

I also included a `WebApplicationExceptionMapper` to deal with basic HTTP mistakes. If someone tries to access a URL that doesn't exist, or sends data in the wrong format, the server won't just crash or return a default web server error page. Instead, it catches those bad requests and sends back the exact right response—like a standard 404 Not Found for bad paths, or a 415 when the media type is wrong. It’s a small addition, but it makes the API feel much more robust and professional when handling bad traffic

For unexpected server-side errors, `GlobalExceptionMapper` returns a safe `500 Internal Server Error` response without exposing internal Java stack trace details.

Example error response - 

```json
{
  "error": "Room Conflict",
  "status": 409,
  "message": "Room cannot be deleted because it still has sensors assigned"
}
```

The global exception mapper is used as a safety net so that unexpected Java errors do not expose stack traces to the client.

## Logging

The project includes a JAX-RS logging filter.

The `LoggingFilter` logs:

- incoming request HTTP method
- incoming request URI
- outgoing response status code

This keeps logging in one place instead of repeating `Logger.info()` statements inside every resource method.

# Report: Answers to Coursework Questions
## Part 1 — Service Architecture and Discovery
### 1.1 JAX-RS Resource Lifecycle and In-Memory Data Management

By default JAX RS resource classes are usually request scoped. This means that a new resource object is normally created for each incoming HTTP request.

This matters because data stored as normal instance variables inside resource classes would not be reliable across multiple requests. For example, if a POST request added a room to an instance variable inside `RoomResource`, another GET request could be handled by a different resource object and may not see that data.

To avoid this problem, this project keeps shared application data inside a separate in-memory `Database` class. The resource classes use that shared storage when handling room, sensor, and reading operations.

Because this data is shared across requests, race conditions can happen if many clients access or update the same collections at the same time. In a larger production API, thread-safe collections such as `ConcurrentHashMap` or synchronized access would be important. For this coursework project, the in-memory storage is kept simple while still separating shared state from request-scoped resource objects.

### 1.2 HATEOAS and the Value of Hypermedia

Hypermedia in REST means that API responses can include links to related resources. This helps clients discover available actions and endpoints from the API response itself.

In this project the discovery endpoint at `GET /api/v1` returns API metadata and links to important resources such as rooms and sensors. This gives the client one clear starting point for the API.

This is useful because client developers do not need to guess all paths manually. Static documentation is still useful but hypermedia makes the running API more self-descriptive and easier to navigate.

## Part 2 — Room Management
### 2.1 Returning IDs vs Full Room Objects

When returning a list of rooms, the API could return only room IDs or it could return full room objects.

Returning only IDs makes the response smaller and saves bandwidth. However, the client would then need to send extra requests to get each rooms name capacity and sensor list. This can increase client-side work and create more network calls.

Returning full room objects gives the client all important room details in one response. The response is larger but it is easier for clients to process and easier to demonstrate in a small coursework API.

In this project full room objects are returned because the API is small and the response is more useful for testing and demonstration.

### 2.2 Is DELETE Idempotent in This Implementation

Yes the DELETE operation is idempotent in terms of final server state.

If a room has sensors assigned to it, the API blocks the deletion and returns `409 Conflict`. Sending the same DELETE request again still leaves the room unchanged, so the final state is the same.

If a room has no sensors assigned, the first DELETE request removes it successfully. If the client sends the same DELETE request again, the room is already gone and the API may return `404 Not Found`. Even though the response changes, the final state is still the same because the room remains deleted.

This means repeated DELETE requests do not keep changing the server state.

## Part 3 — Sensor Operations and Filtering
### 3.1 What Happens If the Client Sends the Wrong Content-Type?

The POST methods use `@Consumes(MediaType.APPLICATION_JSON)`, meaning the API expects JSON request bodies.

If a client sends another content type such as `text/plain` or `application/xml`, the JAX-RS runtime can reject the request before it reaches the resource method. The correct response for this situation is `415 Unsupported Media Type`.

This makes the API contract clear because clients know that create requests for rooms, sensors, and readings must be sent as JSON.


### 3.2 Why Use `@QueryParam` Instead of a Path Segment for Filtering?

A query parameter is better for filtering a collection because the main resource is still the collection.

For example - 

```text
/api/v1/sensors?type=CO2
```

This means the client wants the sensors collection, filtered by type.

An alternative path such as - 

```text
/api/v1/sensors/type/CO2
```

makes the filter look like a fixed resource path. That is less flexible.

Query parameters are also easier to extend. For example, the API could later support - 

```text
/api/v1/sensors?type=CO2&status=ACTIVE
```

For this reason this project uses `@QueryParam("type")` to filter sensors by type.

## Part 4 — Sub-Resources
### 4.1 Benefits of the Sub-Resource Locator Pattern

The sub resource locator pattern helps split nested resource logic into separate classes.

In this project sensor readings belong to a specific sensor so the path is - 

```text
/api/v1/sensors/{sensorId}/readings
```

The `SensorResource` class locates the sensor ID and returns a `SensorReadingResource` object to handle the remaining readings path.

This is cleaner than putting all sensor and reading logic into one large class. `SensorResource` focuses on sensor operations, while `SensorReadingResource` focuses on reading history and adding new readings.

This separation makes the code easier to read, easier to maintain and better structured for larger APIs.

## Part 5 — Error Handling, Exception Mapping, and Logging
### 5.1 Why Is HTTP 422 More Accurate Than 404 for a Missing Linked Room?

When a client sends a POST request to create a sensor, the endpoint may be correct and the JSON body may be valid. However, the `roomId` inside the JSON may refer to a room that does not exist.

A `404 Not Found` usually means the requested URL or resource path does not exist. In this case, the URL exists. The problem is inside the request body.

Therefore, `422 Unprocessable Entity` is more accurate because the server understands the request format but cannot process it due to invalid linked data.

### 5.2 Security Risks of Exposing Java Stack Traces

Exposing Java stack traces in API responses is a security risk.

A stack trace can reveal internal package names, class names, method names line numbers, framework details and sometimes file paths. An attacker could use this information to understand the backend structure and search for weaknesses.

This project uses a `GlobalExceptionMapper` to catch unexpected errors and return a safe `500 Internal Server Error` JSON response. The response does not expose raw Java exception details to the client.

### 5.3 Why Use Filters for Logging?

Logging is a cross cutting concern because it applies to many endpoints, not just one resource method.

If logging was added manually inside every method, the code would be repeated many times. It would also be easy to forget logging when adding new endpoints.

This project uses a JAX RS filter that implements request and response filtering. It logs incoming request details and outgoing response status codes in one place.

This keeps the resource classes focused on API logic while the filter handles logging consistently.

## Video Demonstration

Video demonstration link - 

```text
[ADD YOUR VIDEO LINK HERE]
```

The video demonstrates - 

- starting the server
- discovery endpoint
- room operations
- sensor operations and filtering
- nested sensor readings
- custom exception handling
- WebApplicationException handling for normal JAX-RS errors
- request and response logging

