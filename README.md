To clone the repository, enter:
```
git clone https://github.com/melnikanna6766-a11y/ErrorFreeText.git
```
To start docker compose, you can simply run the command: 
```
docker compose up --build
```
You can also pass variable values; otherwise, default values ​​will be assigned.

Variables:
- DB_USER
- DB_PASSWORD
- FIXED_RATE (default - 60000) - scheduler rate
- SERVER_PORT (default - 8080)
- CHARS_LIMIT (default - 10000000) - maximum possible number of characters that can be sent to the speller per day
- EXECUTION_LIMIT (default - 10000) - maximum possible number of execution that can be sent to the speller per day
- REQUEST_LIMIT (default - 10000) - maximum possible number of characters sent to the speller per request

The application has two endpoints:

**POST (/tasks)**

EXAMPLE:
```
curl -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d '{"text": "тесст", "lang": "ru"}'
```
returns the identifier of the saved task, which is required to retrieve the response from the get method.

**GET (tasks/{id})**

EXAMPLE:
```
curl http://localhost:8080/tasks/{id}
```
returns a response containing the task, status, and an error if one occurred.

By default the scheduler runs once a minute. The value is specified by the `FIXED_RATE` variable.

Press Ctrl + C  to finish

for stopping and removing containers
```
docker compose down
```
