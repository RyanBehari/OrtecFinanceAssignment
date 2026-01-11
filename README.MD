# Task List Case – REST API Documentation

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
* `deadline` – optional date (`DD-MM-YYYY`)

## REST Endpoints

### 1. Create a New Project

**POST /projects**

**Request Body:**

```json
{
  "name": "My Project"
}
```

**Response:** `200 OK` (no body)

---

### 2. Get All Projects and Tasks

**GET /projects**

**Response Example:**

```json
{
  "My Project": [
    {
      "id": 1,
      "description": "Write documentation",
      "done": false,
      "deadline": "2026-01-15"
    }
  ],
  "Another Project": []
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
PUT /projects/My Project/tasks/1?deadline=20-01-2026
```

**Response:** `200 OK` (no body)

---

### 5. View Tasks Grouped by Deadline

**GET /projects/view_by_deadline**

**Response Example:**

```json
{
  "20-01-2026": {
    "My Project": [
      {
        "id": 1,
        "description": "Implement REST API",
        "done": false,
        "deadline": "2026-01-20"
      }
    ]
  },
  "No deadline": {
    "Another Project": [
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
* Dates can be given by the user in `DD-MM-YYYY` format but internally stored and serialized by Java as `YYYY-MM-DD`.

