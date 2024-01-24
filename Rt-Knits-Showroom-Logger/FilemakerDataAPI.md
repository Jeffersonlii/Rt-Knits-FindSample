# Enable access to REST API

activate `fmrest extended privilege` in the privilege sets of user accounts

# API docs

`https://host/fmi/data/apidoc/`

# Login to FM

`/fmi/data/version/databases/database-name/sessions`

`Authorization: name:pass`

returns a auth token (expires after 15 mins of no call)

```javascript
Example responce

Header:
  "X-FM-Data-Access-Token": "c4d2e429122e9cdeda19bb23c55cd2a8f282c3cc50c60943a110",
{
Body:
  "messages": [
    {
      "message": "OK",
      "code": "0"
    }
  ],
  "response": {
    "token": "c4d2e429122e9cdeda19bb23c55cd2a8f282c3cc50c60943a110"
  },
  "HTTPMessage": "OK",
  "HTTPCode": 200
}
```

# Create record

`/fmi/data/version/databases/database-name/layouts/layout-name/records`

`Authorization: Bearer c4d2e429122e9cdeda19bb23c55cd2a8f282c3cc50c60943a110`
(Token from login)

```javascript
Body
{"fieldData":
  {
    "SampleId": "12345SG",
    "NumberField": 99.99,
  }
}

```

# Get Record

`/fmi/data/version/databases/database-name/layouts/layout-name/records/record-id`
