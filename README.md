# Selenium + Rest Assured PoC Framework

Java 17 · Maven · TestNG · Selenium 4 · Rest Assured · Allure · LambdaTest · GitHub Actions

## Layout
```
src/main/java/com/poc/framework
  config/     ConfigManager        -D flag > ENV var > config.properties
  driver/     DriverFactory        local (chrome/firefox/edge) or LambdaTest remote
              DriverManager        ThreadLocal<WebDriver> for parallel runs
  pages/      BasePage + page objects (Page Object Model, explicit waits, @Step)
  api/        ApiSpecs, PostsClient, Post     Rest Assured specs + endpoint clients
  listeners/  TestListener         screenshot on failure, LambdaTest pass/fail flag
  base/       BaseUiTest           driver lifecycle per test method
src/test/java/com/poc/tests/{ui,api}
src/test/resources/suites/{ui,api,all}.xml
.github/workflows/ci.yml
```

## Run locally
```bash
mvn test                                                     # everything, local Chrome
mvn test -Dsuite=src/test/resources/suites/api.xml           # API only
mvn test -Dsuite=src/test/resources/suites/ui.xml -Dbrowser=firefox -Dheadless=true
mvn test -Dgroups=smoke                                      # by TestNG group
```

## Run on LambdaTest
```bash
export LT_USERNAME=your_user
export LT_ACCESS_KEY=your_key
mvn test -Dsuite=src/test/resources/suites/ui.xml \
  -Dexecution=lambdatest -Dbrowser=chrome -Dlt.platform="Windows 11" -Dlt.build=my-build
```
Add `LT_USERNAME` and `LT_ACCESS_KEY` as repository secrets for CI.

## Allure report
```bash -- npm based
npm install -g allure-commandline
allure serve target/allure-results -h 127.0.0.1 -p 8000

```bash
mvn allure:serve        # build + open the report from target/allure-results
```
In CI, results from every job are merged and published to GitHub Pages with history/trends.

## Adding things
- New page: extend `BasePage`, add `@Step` to public actions.
- New endpoint: add a method to a `*Client` class; assertions stay in the test.
- New browser/OS combo on LambdaTest: add a row to the matrix in `ci.yml`.
