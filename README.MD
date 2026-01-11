# Task List Case – REST API Documentation

## Running the Application

The application can be run using:

* **Simultaneously start Console (CLI) + REST API mode:**

```
mvn spring-boot:run
```

This will launch the REST API at `http://localhost:8080` and also start the CLI in the same process using shared memory.

## Base URL

```
http://localhost:8080
```

All REST endpoints are prefixed with `/projects`.

## Data Model Overview

### Project

* Identified by a **project name**
* Contains a list of tasks

### Task

Each task has:

* `id` – unique identifier
* `description` – task description
* `done` – boolean indicating completion
* `deadline` – optional date (`DD-MM-YYYY`) (note: internally stored as `YYYY-MM-DD` by Java)

## REST Endpoints

### 1. Create a New Project

**POST /projects**

**Request Body:**

```json
{
  "name": "MyProject"
}
```

**Response:** `200 OK` (no body)

---

### 2. Get All Projects and Tasks

**GET /projects**

**Response Example:**

```json
{
  "MyProject": [
    {
      "id": 1,
      "description": "Write documentation",
      "done": false,
      "deadline": "2026-01-15"
    }
  ],
  "AnotherProject": []
}
```

---

### 3. Create a Task for a Project

**POST /projects/{projectName}/tasks**

**Request Body:**

```json
{
  "description": "Implement REST API"
}
```

**Response:** `200 OK` (no body)

---

### 4. Update Task Deadline

**PUT /projects/{projectName}/tasks/{taskId}?deadline=DD-MM-YYYY**

**Example:**

```
PUT /projects/MyProject/tasks/1?deadline=20-01-2026
```

**Response:** `200 OK` (no body)

---

### 5. View Tasks Grouped by Deadline

**GET /projects/view_by_deadline**

**Response Example:**

```json
{
  "20-01-2026": {
    "MyProject": [
      {
        "id": 1,
        "description": "Implement REST API",
        "done": false,
        "deadline": "2026-01-20"
      }
    ]
  },
  "No deadline": {
    "AnotherProject": [
      {
        "id": 2,
        "description": "Refactor code",
        "done": false,
        "deadline": null
      }
    ]
  }
}
```

---

## Notes

* Data is **in memory** and lost when the application stops.
* Dates are displayed to the user in `DD-MM-YYYY` format but internally stored and serialized by Java as `YYYY-MM-DD`.
* Console application and REST API share the same memory when run simultaneously.
