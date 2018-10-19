## Instructions

```$xslt
1. Edit .gitignore, add an ignore for each apiKey-set from a source, as shown below and COMMIT

src/keys/<source>_api.json

2. Add a <source>_api.json file inside src/keys
3. Add your api keys in the following form

{
    "apiKey": "",
    "apiSecretKey": "",
    "accessToken": "",
    "accessTokenSecret": ""
}

4. GameManager should have a function to extract these values
__* Note that the apiKey.class must have the same keys in the json file__

class apiKey{
    String apiKey;
    String apiSecretKey;
    String accessToken;
    String accessTokenSecret;
}
