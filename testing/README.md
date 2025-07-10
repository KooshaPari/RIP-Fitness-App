# Fitness App Testing Framework

## Overview

This comprehensive testing framework provides complete test coverage for the Fitness App with KMobile integration for device automation and performance monitoring.

## Test Structure

```
testing/
├── kmobile/                    # KMobile configuration
│   └── KMobileConfig.yaml     # Device and test configuration
├── src/main/java/             # Testing utilities
│   └── com/fitnessapp/testing/
│       ├── HiltTestRunner.kt   # Custom test runner
│       ├── KMobileTestSetup.kt # KMobile setup rule
│       ├── TestData.kt         # Test data factory
│       ├── TestDatabase.kt     # In-memory database setup
│       └── MockNetworkModule.kt # Network mocking
├── scripts/
│   └── run_all_tests.sh       # Comprehensive test runner
└── reports/                   # Generated test reports
    ├── unit/                  # Unit test reports
    ├── integration/           # Integration test reports
    ├── ui/                    # UI test reports
    ├── e2e/                   # End-to-end test reports
    ├── performance/           # Performance test reports
    ├── security/              # Security test reports
    ├── accessibility/         # Accessibility test reports
    └── kmobile/               # KMobile reports
```

## Test Categories

### 1. Unit Tests (`app/src/test/`)
- **Database Tests**: Room database operations, migrations, complex queries
- **Algorithm Tests**: BMR/TDEE calculations, progress analytics, nutrition algorithms
- **Business Logic Tests**: Core functionality without Android dependencies
- **Location**: `app/src/test/java/com/fitnessapp/android/unit/`

### 2. Integration Tests (`app/src/androidTest/`)
- **Health Integration**: HealthConnect, Google Fit, Samsung Health
- **Network Integration**: API integrations with proper error handling
- **Database Integration**: Real database operations with Android context
- **Location**: `app/src/androidTest/java/com/fitnessapp/android/integration/`

### 3. UI Tests (`app/src/androidTest/`)
- **Compose UI Tests**: Navigation, user interactions, form inputs
- **Screen Tests**: Individual screen functionality
- **Theme Tests**: Dark mode, accessibility themes
- **Location**: `app/src/androidTest/java/com/fitnessapp/android/ui/`

### 4. End-to-End Tests (`app/src/androidTest/`)
- **Complete User Workflows**: Onboarding → First workout → Nutrition logging
- **Cross-feature Integration**: Health sync + workout + nutrition
- **Offline/Online Scenarios**: Data persistence and sync
- **Location**: `app/src/androidTest/java/com/fitnessapp/android/e2e/`

### 5. Performance Tests (`app/src/androidTest/`)
- **App Startup Time**: Cold/warm/hot start benchmarks
- **Database Performance**: Query optimization, large dataset handling
- **Memory Usage**: Memory leak detection, allocation patterns
- **Battery Usage**: Background activity monitoring
- **Location**: `app/src/androidTest/java/com/fitnessapp/android/performance/`

### 6. Security Tests (`app/src/androidTest/`)
- **Data Encryption**: At-rest and in-transit encryption
- **Authentication**: Biometric, PIN, session management
- **Network Security**: Certificate pinning, SSL validation
- **Code Protection**: Obfuscation, anti-tampering
- **Location**: `app/src/androidTest/java/com/fitnessapp/android/security/`

### 7. Accessibility Tests (`app/src/androidTest/`)
- **Screen Reader Support**: TalkBack compatibility
- **Keyboard Navigation**: Full keyboard accessibility
- **Touch Targets**: Minimum 48dp touch targets
- **Color Contrast**: WCAG AA compliance
- **Text Scaling**: Support for large text sizes
- **Location**: `app/src/androidTest/java/com/fitnessapp/android/accessibility/`

## KMobile Integration

### Configuration
KMobile is configured via `testing/kmobile/KMobileConfig.yaml`:

```yaml
devices:
  physical:
    - name: "pixel-7-pro"
      model: "Pixel 7 Pro"
      capabilities: ["health-connect", "biometric", "camera"]
  emulators:
    - name: "api-34-emulator"
      api: 34
      target: "google_apis"

testSuites:
  performance:
    enabled: true
    metrics: ["cpu", "memory", "battery", "network"]
  security:
    enabled: true
    staticAnalysis: true
    dynamicAnalysis: true
```

### Features
- **Device Automation**: Automated testing across multiple devices
- **Performance Monitoring**: Real-time CPU, memory, battery tracking
- **Security Testing**: Automated vulnerability scanning
- **Report Generation**: Comprehensive HTML and JSON reports

## Running Tests

### Quick Start
```bash
# Run all tests
./testing/scripts/run_all_tests.sh

# Run specific test suite
./gradlew testDebugUnitTest                    # Unit tests
./gradlew connectedDebugAndroidTest            # All Android tests
```

### Individual Test Categories
```bash
# Unit tests
./gradlew testDebugUnitTest

# Integration tests
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.integration

# UI tests
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.ui

# Performance tests
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.performance

# Security tests
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.security

# Accessibility tests
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.accessibility
```

### CI/CD Integration
```bash
# GitHub Actions
./testing/scripts/run_all_tests.sh --ci --upload-reports

# Jenkins
./testing/scripts/run_all_tests.sh --jenkins --workspace $WORKSPACE
```

## Test Data

### Test Data Factory
Use `TestData` object for consistent test data:

```kotlin
// Create test user
val user = TestData.createTestUser(
    name = "John Doe",
    age = 30,
    heightCm = 175f
)

// Create test workout
val workout = TestData.createTestWorkout(userId = user.id)

// Create test weights (7 days)
val weights = TestData.createTestWeights(7, userId = user.id)
```

### Database Setup
```kotlin
@Before
fun setup() {
    database = TestDatabase.createInMemoryDatabase()
    TestDatabase.populateWithTestData(database)
}
```

## Mocking

### Network Mocking
```kotlin
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object MockNetworkModule {
    @Provides
    fun provideMockNutritionApi(): NutritionApi = mockk {
        coEvery { searchFoods(any()) } returns mockFoodResponse
    }
}
```

### KMobile Test Rule
```kotlin
@get:Rule
val kmobileRule = KMobileTestSetup()

@Test
fun testWithKMobile() {
    val deviceManager = kmobileRule.getDeviceManager()
    val performanceMonitor = kmobileRule.getPerformanceMonitor()
    
    performanceMonitor.startTrace("test_operation")
    // Test code
    performanceMonitor.endTrace("test_operation")
}
```

## Performance Benchmarking

### Benchmark Tests
```kotlin
@Test
fun benchmarkDatabaseQuery() {
    benchmarkRule.measureRepeated {
        val results = database.workoutDao().getWorkouts(limit = 100)
        assert(results.isNotEmpty())
    }
}
```

### Performance Targets
- **App Startup**: < 2.5 seconds (cold start)
- **Database Queries**: < 100ms (complex queries)
- **UI Rendering**: < 16ms per frame (60fps)
- **Memory Usage**: < 256MB heap
- **Battery Usage**: < 5% per hour (background)

## Security Testing

### Security Checks
- Data encryption (AES-256)
- Certificate pinning validation
- SQL injection protection
- Code obfuscation verification
- Runtime protection (anti-debugging)

### Security Targets
- Overall security score: ≥ 8.0/10
- Zero critical vulnerabilities
- ≤ 2 high severity issues

## Accessibility Standards

### WCAG Compliance
- **Level AA**: Color contrast ratio ≥ 4.5:1
- **Touch Targets**: Minimum 48dp
- **Screen Reader**: Full TalkBack support
- **Keyboard Navigation**: Complete keyboard accessibility
- **Text Scaling**: Support up to 200% scaling

## Reporting

### Generated Reports
- **HTML Summary**: Comprehensive test overview
- **JUnit XML**: CI/CD integration
- **Performance Metrics**: KMobile JSON reports
- **Security Scan**: Vulnerability assessment
- **Accessibility Audit**: WCAG compliance report

### Report Locations
```
testing/reports/
├── test_summary_YYYYMMDD_HHMMSS.html    # Main summary
├── unit/index.html                       # Unit test details
├── integration/index.html                # Integration test details
├── ui/index.html                        # UI test details
├── e2e/index.html                       # E2E test details
├── performance/index.html               # Performance benchmarks
├── security/index.html                  # Security scan results
├── accessibility/index.html             # Accessibility audit
└── kmobile/
    ├── performance_report_*.html         # KMobile performance
    └── metrics_*.json                    # Raw metrics data
```

## Best Practices

### Test Organization
1. **Single Responsibility**: One test, one assertion
2. **Descriptive Names**: Clear test method names
3. **Arrange-Act-Assert**: Structured test organization
4. **Test Data**: Use TestData factory for consistency

### Performance Testing
1. **Baseline Measurements**: Establish performance baselines
2. **Regression Detection**: Monitor performance changes
3. **Real Device Testing**: Test on actual hardware
4. **Memory Profiling**: Monitor memory usage patterns

### Accessibility Testing
1. **Automated Checks**: Use accessibility scanning tools
2. **Manual Testing**: Test with actual screen readers
3. **User Testing**: Include users with disabilities
4. **Continuous Monitoring**: Regular accessibility audits

## Troubleshooting

### Common Issues

#### KMobile Setup
```bash
# Install KMobile CLI
npm install -g @kmobile/cli

# Initialize configuration
kmobile init --config testing/kmobile/KMobileConfig.yaml
```

#### Device Connection
```bash
# Check connected devices
adb devices

# Start emulator
$ANDROID_HOME/emulator/emulator -avd Pixel_8_API_34
```

#### Test Failures
```bash
# Clear app data
adb shell pm clear com.fitnessapp.android.debug

# Reset emulator
adb -e shell "setprop sys.boot_completed 0 && stop && start"
```

## Continuous Integration

### GitHub Actions Example
```yaml
name: Comprehensive Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
      - name: Run Tests
        run: ./testing/scripts/run_all_tests.sh --ci
      - name: Upload Reports
        uses: actions/upload-artifact@v3
        with:
          name: test-reports
          path: testing/reports/
```

## Contributing

### Adding New Tests
1. Follow existing patterns in each test category
2. Use TestData factory for test data
3. Include KMobile integration where appropriate
4. Update this README with new test descriptions

### Test Maintenance
1. Review and update test data regularly
2. Monitor performance baselines
3. Update security checks for new threats
4. Ensure accessibility compliance with latest standards

## Dependencies

### Testing Libraries
- JUnit 4.13.2
- AndroidX Test 1.5.0
- Espresso 3.5.1
- Compose Testing
- MockK 1.13.8
- Truth 1.1.4
- Robolectric 4.11.1

### KMobile Integration
- KMobile SDK 1.2.0
- Device Automation 1.2.0
- Performance Testing 1.2.0
- Security Testing 1.2.0

### Performance
- AndroidX Benchmark 1.2.2
- UI Automator 2.2.0

### Security
- Detekt 1.23.4
- Accessibility Test Framework 4.0.0

## Support

For issues with the testing framework:
1. Check this README for common solutions
2. Review KMobile documentation
3. Check individual test logs in reports
4. Create issue with detailed error information