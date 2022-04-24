# Getting Started 

Run **_start.bat** to start the application automatically (building project, testing, building docker image, start in docker)

or do these steps manually

1) Run `mvnw clean package`
2) Run `docker build -t employee-state-machine .`
3) Run `docker-compose up`

**Swagger:** http://localhost:8080/state-machine/swagger-ui.html