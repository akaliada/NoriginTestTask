## Norigin Test Task
This app is an Android implementation of [Norigin Media testing task](https://github.com/NoriginMedia/candidate-tester) for new candidates.

---

#### Requirements:

Before running this project you need to setup [EPG mock-api](https://github.com/NoriginMedia/candidate-tester/blob/master/README.md#mock-api) 
and specify mock server URL at build config field `API_URL` in `app/build.gradle` file:

```
defaultConfig {
    ...

    buildConfigField 'String', 'API_URL', '"http://10.10.2.123:1337"'

}
```