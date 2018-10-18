## Instructions

```$xslt
1. Edit .gitignore, add an ignore for each api from a source, as shown below and COMMIT
src/keys/<source>_api.json

2. Add a <source>_api.json file inside src/keys
3. Add your api keys in the following form

{
    "api-key": "",
    "api-secret-key": "",
    "access-token": "",
    "access-token-secret": ""
}

4. _Starter should have a function to extract these values
* Note that the apiKey.class has the same keys in the json file