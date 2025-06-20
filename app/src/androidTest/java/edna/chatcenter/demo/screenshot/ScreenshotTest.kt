package edna.chatcenter.demo.screenshot

/**
 * https://github.com/dropbox/dropshots
 * For recording screenshots for tests: ./gradlew recordScreenshots
 * For testing: ./gradlew verifyScreenshots
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScreenshotTest
