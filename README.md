# hackathon-locations-backend

Simple backend template:

* GET /all returns all data from database (application/json).
* POST /location pushes location data to database (application/json).

Examples:

````````````````````````````````````````````````````````````````````````````````
$ echo Posting data
$ curl -v -H "Content-Type: application/json" -X POST -d "{\"longitude\": 1.0, \"latitude\": 2.0, \"altitude\": 0 }" localhost:8080/location
$ curl -v -H "Content-Type: application/json" -X POST -d "{\"longitude\": 2.0, \"latitude\": 3.0, \"altitude\": 0 }" localhost:8080/location
$ echo Getting data
$ curl -v -X GET localhost:8080/all
````````````````````````````````````````````````````````````````````````````````
