# ATAF mobile Android demo

Example consumer project for the ATAF `mobile` module. It runs one Cucumber scenario against Appium's public [ApiDemos](https://www.npmjs.com/package/android-apidemos) app (Apache-2.0) on a physical Android device or an emulator.

The module is part of the Maven reactor so it is compiled in every build, but it is never published and its scenarios only run with the `android-device` profile.

| Path                                           | Content                                                                 |
| ---------------------------------------------- | ----------------------------------------------------------------------- |
| `src/test/resources/features/`                 | Feature file tagged `@mobile`                                           |
| `src/test/java/example/mobile/apidemos/`       | `ApiDemosRunner` (extends `BasicTestNGRunner`), page objects, steps     |
| `src/test/resources/cucumber.properties`       | Features, glue (`ataf.mobile.steps` + example steps), report plugins    |
| `src/test/resources/testautomation.properties` | Appium URL, app package/activity, timeouts (UDID and APK path via `-D`) |
| `github-actions/android-device-tests.yml`      | Inactive workflow template for a self-hosted device runner              |

## Run it

Prerequisites: JDK 21, Maven, a running Appium 3 server with the UiAutomator2 driver 8.x, and a device listed by `adb devices`. See the [mobile handbook page](https://it-at-m.github.io/agile-test-automation-framework/usage/mobile-android.html) for the full setup.

1. Download the APK into a folder outside the repository, so it can't be committed by accident:

   ```bash
   npm pack android-apidemos@5.0.0 && tar -xzf android-apidemos-5.0.0.tgz package/apks/ApiDemos-debug.apk
   ```

2. Install the framework modules from the repository root (once, and again after framework changes):

   ```bash
   mvn install -DskipTests
   ```

3. Run only the demo module, with the absolute path of the APK:

   ```bash
   mvn -pl examples/mobile-android-demo verify -Pandroid-device -Dtestautomation.mobile.udid=<adb serial> -Dtestautomation.mobile.app=<absolute path to ApiDemos-debug.apk>
   ```

Don't add `-am` to step 3: `-D` values apply to every module in the build, so the device settings would also reach the unit tests of `core` and `mobile`. IntelliJ's Maven run configurations don't expand shell variables such as `$PWD`, and PowerShell needs each `-D` argument in quotes.

Reports: `target/cucumber-reports/cucumber.json` (Cucumber JSON), `cucumber.xml` (JUnit XML), `cucumber.html`, plus `target/surefire-reports/` and per-scenario logs in `logs/`. A failed scenario fails the Maven build (non-zero exit code).
