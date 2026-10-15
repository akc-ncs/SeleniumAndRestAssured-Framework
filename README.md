# Selenium and Rest Assured Test Framework

Java 17, Maven, TestNG, Selenium 4, Rest Assured, Allure, and LambdaTest.

## Project Structure

```text
src/main/java/com/poc/framework/
  api/        API request/response specs and endpoint client/model
  base/       Shared UI test setup and teardown
  config/     Property, environment-variable, and system-property resolution
  driver/     BrowserTarget, DriverFactory, and thread-local DriverManager
  listeners/  Allure screenshot and LambdaTest status reporting
  pages/      Page objects and shared Selenium page helpers
src/main/resources/config.properties
src/test/java/com/poc/tests/
  api/        REST API tests
  ui/         Web UI tests
src/test/resources/suites/
  all.xml                     API and UI tests
  api.xml                     API tests only
  ui.xml                      UI tests only
  lambdatest_cross_browser.xml Chrome, Edge, and Safari in parallel
.github/workflows/
  ci.yml                      API tests, local headless Chrome, and Allure artifact
  lambdatest.yml              LambdaTest cross-browser run and Allure artifacts
```

Each UI test obtains its browser target from TestNG suite parameters. `DriverManager`
stores the WebDriver in a `ThreadLocal`, allowing the TestNG suites to execute tests
in parallel without sharing browser sessions.

## Prerequisites

- JDK 17
- Maven 3.8 or newer
- A supported local browser for local UI tests: Chrome, Edge, or Firefox
- LambdaTest credentials to run the cross-browser suite

Safari is run remotely on LambdaTest with macOS; it is not launched as a local
browser by this project's current driver factory.

## Run Tests Locally

The default Maven suite runs API and UI tests. The default browser is headless Chrome.

```bash
mvn test
```

Run one suite or select another supported local browser:

```bash
mvn test -Dsuite=src/test/resources/suites/api.xml
mvn test -Dsuite=src/test/resources/suites/ui.xml -Dbrowser=edge -Dheadless=true
mvn test -Dsuite=src/test/resources/suites/ui.xml -Dbrowser=firefox -Dheadless=true
```

## Run Cross-Browser Tests on LambdaTest

Set the LambdaTest credentials as environment variables. The Maven profile selects
the parallel cross-browser TestNG suite, which runs Chrome and Edge on Windows 11 and
Safari on macOS Sonoma.

```bash
export LT_USERNAME=your_username
export LT_ACCESS_KEY=your_access_key
mvn test -Plambdatest_crossbrowser -Dlt.build=my-build
```

The account must allow at least three concurrent browser sessions. Configure
`LT_USERNAME` and `LT_ACCESS_KEY` as repository Actions secrets for CI. Pull requests
from forks do not receive repository secrets, so the LambdaTest workflow will stop at
its credential check for those events.

The browser, platform, and version matrix is declared in
`src/test/resources/suites/lambdatest_cross_browser.xml`. Add or change a TestNG
`<test>` entry there to change a browser target.

## Configuration

Configuration values resolve in this order: Java system property (`-Dkey=value`),
environment variable (uppercase key with dots changed to underscores), then
`src/main/resources/config.properties`.

| Setting | Default | Purpose |
| --- | --- | --- |
| `execution` | `local` | `local` or `lambdatest_crossbrowser` |
| `browser` | `chrome` | Local browser selection |
| `headless` | `true` | Headless mode for local browser runs |
| `base.url` | `https://www.saucedemo.com` | UI application URL |
| `api.base.url` | `https://jsonplaceholder.typicode.com` | API base URL |
| `timeout.seconds` | `15` | Explicit Selenium wait timeout |
| `lt.hub` | LambdaTest hub URL | Remote WebDriver endpoint |
| `lt.browser.version` | `latest` | Remote browser version fallback |
| `lt.build` | `local-build` | LambdaTest build label |

LambdaTest credentials are read from `LT_USERNAME` and `LT_ACCESS_KEY`.

## Allure reports

Tests write their raw results to `target/allure-results`. The location is set in
`src/test/resources/allure.properties`:

```properties
allure.results.directory=target/allure-results
```

### View a report locally

Install the Allure CLI once (it needs Java, which the project already requires):

```bash
npm install -g allure-commandline
```

Run the tests, then build and serve the report:

```bash
mvn clean test
allure serve target/allure-results -h 127.0.0.1 -p 8000
```

Open <http://localhost:8000>. `allure serve` builds the report once when it
starts, so stop it with `Ctrl+C` and run it again after each new test run.

**Working on a remote server over SSH?** The command above only listens on the
server's localhost. Forward the port to your laptop, either with the VS Code
**Ports** tab or with `ssh -L 8000:localhost:8000 <user>@<server>`, then open
<http://localhost:8000> on your laptop.

To keep a report folder instead of serving one:

```bash
allure generate target/allure-results --clean -o allure-report
allure open allure-report -h 127.0.0.1 -p 8000
```

> A report cannot be opened by double-clicking `index.html`. It must be served
> over HTTP, which `allure serve` and `allure open` do for you.

If the `allure-maven` plugin is still in `pom.xml`, `mvn allure:serve` is an
alternative, but the Allure CLI above is the recommended route.

### Reports in CI

Both GitHub Actions workflows build an Allure report from the test results and
upload it as a downloadable artifact. Neither publishes it to GitHub Pages.

| Workflow          | Artifacts |
| ---               | ---       |
| `ci.yml`          | `allure-results-api`, `allure-results-ui`, and the merged `allure-report` |
| `lambdatest.yml`  | `allure-report` and `allure-results` |

To read a CI report, download the `allure-report` artifact from the run page,
unzip it, and run `allure open <unzipped-folder>`.


## Adding Tests

- Add page objects under `src/main/java/com/poc/framework/pages` and extend `BasePage`.
- Add API endpoint methods to the relevant client; keep assertions in the tests.
- Add test classes under `src/test/java/com/poc/tests/api` or `ui`.
- Add browser targets to `lambdatest_cross_browser.xml` to expand the remote matrix.
