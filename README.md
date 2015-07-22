# hackathon-locations-backend

Simple backend for hazardous hackathon app.

#### Report issue:

    GET /hazard/issue/report/${watchid}?cmd=post&lat=-xx.xxxxxx&lon=-xx.xxxxxx&severity=1&heading=xxx.xxx&speed=xx.xx

Response:

    {
        "code": 0,
        "description": "Success"
    }

severity 1 means orange, 2 means red

#### Get amount of issues (preroute view (dots)):

    GET /hazard/issue/count/${watchid}?cmd=get&lat=-xx.xxxxxx&lon=-xx.xxxxx&heading=xxx.xxx&radius=xxx.xx

Response JSON

    {
        "orange": {
            "up": 10,
            "right": 2,
            "down": 2,
            "left": 0
        },
        "red": {
            "up": 0,
            "right": 0,
            "down": 0,
            "left": 1
        }
    }

#### Get issues:

##### Next issue:

    GET /hazard/issue/next/${watchid}?cmd=get&lat=-xx.xxxxxx&lon=-xx.xxxxxx&heading=xxx.xxx&radius=xxx.xx

There is issue, response:

    {
        "count": 1,
        "id": "xxxxxxxxxxxxxxxxxx", // string id
        "severity": 1, // 1 - orange, 2 - red
        "type": 1, // type of the hazard
        "latitude": 11.111,
        "longitude": 11.111,
        "distance": 100 // meters
    }

There is no issue, response:

    {
        "count": 0
    }

#### Confirm or dismiss issue:

    GET /hazard/issue/confirm/${watchid}/?cmd=post&id=xxxxxx&confirm=1

confirm 1 means yes, 0 means dismiss to ignore do not send report

#### Get reported issues since some moment:

    GET /hazard/issue/new/${watchid}?cmd=get&since=xxxxxxx

There is issues, response:

    {
        "count": 2,
        "issues": [ // array of issues in order of timestamps
            {
                "timestamp: xxxxxxx,
                "id": "xxxxxxxxxxxxxxxxxx", // string id
                "severity": 1, // 1 - orange, 2 - red
                "type": 0, // type of the hazard, 0 - undefined
                "latitude": 11.111,
                "longitude": 11.111,
            },
            {
                "timestamp: xxxxxxx,
                "id": "xxxxxxxxxxxxxxxxxx", // string id
                "severity": 1, // 1 - orange, 2 - red
                "type": 0, // type of the hazard, 0 - undefined
                "latitude": 11.111,
                "longitude": 11.111,
            }
        ]
    }

There is no issues, response:

    {
        "count": 0
    }

#### Update issue:

    GET /hazard/issue/update/${watchid}?cmd=post&id=xxxxxxxxxxx&severity=2&type=2

#### Get route and amount of issues on it:

    GET /hazard/issue/onroute/${watchid}?cmd=get&lat=-xx.xxxxxx&lon=-xx.xxxxxx&heading=xxx.xxx

Response:

    {
        "orange": 1,
        "red": 0,
        "from": "Invalidenstrasse 116, 10115 Berlin",
        "to": "Machnower Str 10, 14165 Berlin"
    }

#### Start traveling

    GET /hazard/watch/start/${watchid}?cmd=post&lat=xx.xxxxxx&lon=xx.xxxxxx&heading=xxx.xx

#### Post last position of the watch

    GET /hazard/watch/report${watchid}?cmd=post&lat=xx.xxxxxx&lon=xx.xxxxxx&heading=xxx.xx&speed=xx.xxx

#### Stop traveling

    GET /hazard/watch/stop/${watchid}?cmd=post&lat=xx.xxxxxx&lon=xx.xxxxxx

#### Get cumulative distance

    GET /hazard/watch/distance/${watchid}

Response:

    {
        "split": 100.2, // distance for last part of the route (part between two last reposting)
        "total": 12030.2 // total traveled distance
    }

#### Get last known position of the watch

    GET /hazard/watch/last/${watchid}

#### Examples

````````````````````````````````````````````````````````````````````````````````

hazard-vivo 15700324b410fee6e51389d698f378b7357bc249

http://localhost:8080/hazard/issue/report/15700324b410fee6e51389d698f378b7357bc249?cmd=post&lat=38.893123&lon=-94.893123&severity=1&heading=0.0&speed=0.0

http://localhost:8080/hazard/issue/count/15700324b410fee6e51389d698f378b7357bc249?cmd=get&lat=38.893123&lon=-94.893123&heading=0.0&radius=1000

http://localhost:8080/hazard/issue/next/15700324b410fee6e51389d698f378b7357bc249?cmd=get&lat=38.893123&lon=-94.893123&heading=0.0&radius=1000

http://localhost:8080/hazard/issue/confirm/15700324b410fee6e51389d698f378b7357bc249?cmd=post&id=55ae31ccff7f1d202fe5ebdf&confirm=1

http://localhost:8080/hazard/issue/new/15700324b410fee6e51389d698f378b7357bc249?cmd=get&since=0

http://localhost:8080/hazard/issue/update/15700324b410fee6e51389d698f378b7357bc249?cmd=post&id=55ae321dff7f1d202fe5ebe0&severity=2&type=2

http://localhost:8080/hazard/watch/start/15700324b410fee6e51389d698f378b7357bc249?cmd=post&lat=52.526768&lon=13.393261&heading=0.0

http://localhost:8080/hazard/watch/report/15700324b410fee6e51389d698f378b7357bc249?cmd=post&lat=52.527768&lon=13.394261&heading=0.0&speed=10.0

http://localhost:8080/hazard/watch/last/15700324b410fee6e51389d698f378b7357bc249

````````````````````````````````````````````````````````````````````````````````

