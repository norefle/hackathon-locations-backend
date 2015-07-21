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

    GET /hazard/issue/confirm/${watchid}/?cmd=post&id=%22xxxxxx%22&confirm=1

confirm 1 means yes, 0 means dismiss to ignore do not send report

#### Get reported issues since some moment:

    GET /hazard/issue/new/${watchid}?cmd=get?since=xxxxxxx

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

    GET /hazard/issue/update/${watchid}?cmd=post?id%22xxxxxxxxxxx%dd&severity=2&type=2

#### Get route and amount of issues on it:

    GET /hazard/issue/onroute/${watchid}?cmd=get&lat=-xx.xxxxxx&lon=-xx.xxxxxx&heading=xxx.xxx

Response:

    {
        "orange": 1,
        "red": 0,
        "from": "Invalidenstrasse 116, 10115 Berlin",
        "to": "Machnower Str 10, 14165 Berlin"
    }
