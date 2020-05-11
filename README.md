# Book Service Example

[![CircleCI](https://circleci.com/gh/xirc/example-micronaut-bookman.svg?style=shield)](https://circleci.com/gh/xirc/example-micronaut-bookman)

## Technical spec
* Micronaut <https://micronaut.io/>
* Kotlin <https://kotlinlang.org/>
* Flyway <https://flywaydb.org/>
* Exposed <https://github.com/JetBrains/Exposed/>  
  Should I use `JOOQ` or `Micronaut Data` instead of this???

## Development Environment
* Kubernates
* Skaffold

## How to run

```
minikube start
skaffold dev -p local --port-forward
```

You can access
* Micronaut API Server  
<http://localhost:8080>
* MySQL Server  
<http://localhost:9000>  
`mysql -u user -P 9000 -D bookman -p`  
Password is `password`.

## API Endpoints

Here are API Endpoints.
```
GET    /books
POST   /books
GET    /books/{id}
PATCH  /books/{id}
DELETE /books/{id}

GET    /persons
POST   /persons
GET    /persons/{id}
PATCH  /persons/{id}
DELETE /persons/{id}

GET /books/search
GET /persons/search
```

Files `example/*.http` contains example requests.  
I uses `REST Client`
<https://marketplace.visualstudio.com/items?itemName=humao.rest-client>

`micronaut-openapi` generates swagger spec files.  
But, I want to write swagger spec, and then generate code!

## Note

* Set MySQL TimeZone to UTC.
* Search implementations are awful...  
Use other solutions such as Elasticsearch in production.
* DB migration can be more cool imo.