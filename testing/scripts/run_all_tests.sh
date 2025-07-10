#!/bin/bash

# Comprehensive Test Runner for Fitness App
# This script runs all test suites with KMobile integration

set -e

echo "🧪 Starting Comprehensive Test Suite for Fitness App"
echo "=================================================="

# Configuration
PROJECT_DIR="$(pwd)"
REPORT_DIR="$PROJECT_DIR/testing/reports"
KMOBILE_CONFIG="$PROJECT_DIR/testing/kmobile/KMobileConfig.yaml"
DATE=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Create report directories
mkdir -p "$REPORT_DIR"/{unit,integration,ui,e2e,performance,security,accessibility,kmobile}

echo -e "${BLUE}📋 Test Configuration:${NC}"
echo "  📁 Project Directory: $PROJECT_DIR"
echo "  📊 Report Directory: $REPORT_DIR"
echo "  ⚙️  KMobile Config: $KMOBILE_CONFIG"
echo "  📅 Test Run ID: $DATE"
echo ""

# Function to run tests with error handling
run_test_suite() {
    local suite_name=$1
    local gradle_task=$2
    local report_subdir=$3
    
    echo -e "${YELLOW}🔬 Running $suite_name Tests...${NC}"
    
    if ./gradlew "$gradle_task" \
        -Pkotlin.incremental=false \
        -Pkmobile.config="$KMOBILE_CONFIG" \
        -Ptest.report.dir="$REPORT_DIR/$report_subdir" \
        --info; then
        echo -e "${GREEN}✅ $suite_name Tests: PASSED${NC}"
        return 0
    else
        echo -e "${RED}❌ $suite_name Tests: FAILED${NC}"
        return 1
    fi
}

# Initialize KMobile
echo -e "${BLUE}🚀 Initializing KMobile Test Environment...${NC}"
if command -v kmobile &> /dev/null; then
    kmobile init --config "$KMOBILE_CONFIG"
    kmobile devices setup
    echo -e "${GREEN}✅ KMobile initialized successfully${NC}"
else
    echo -e "${YELLOW}⚠️  KMobile CLI not found, using embedded SDK${NC}"
fi
echo ""

# Test results tracking
declare -A test_results
total_tests=0
passed_tests=0

# 1. Unit Tests
echo -e "${BLUE}==================${NC}"
echo -e "${BLUE}1️⃣  UNIT TESTS${NC}"
echo -e "${BLUE}==================${NC}"

if run_test_suite "Unit" "testDebugUnitTest" "unit"; then
    test_results["unit"]="PASSED"
    ((passed_tests++))
else
    test_results["unit"]="FAILED"
fi
((total_tests++))

# 2. Integration Tests
echo -e "${BLUE}========================${NC}"
echo -e "${BLUE}2️⃣  INTEGRATION TESTS${NC}"
echo -e "${BLUE}========================${NC}"

if run_test_suite "Integration" "connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.integration" "integration"; then
    test_results["integration"]="PASSED"
    ((passed_tests++))
else
    test_results["integration"]="FAILED"
fi
((total_tests++))

# 3. UI Tests
echo -e "${BLUE}===============${NC}"
echo -e "${BLUE}3️⃣  UI TESTS${NC}"
echo -e "${BLUE}===============${NC}"

if run_test_suite "UI" "connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.ui" "ui"; then
    test_results["ui"]="PASSED"
    ((passed_tests++))
else
    test_results["ui"]="FAILED"
fi
((total_tests++))

# 4. End-to-End Tests
echo -e "${BLUE}==================${NC}"
echo -e "${BLUE}4️⃣  END-TO-END TESTS${NC}"
echo -e "${BLUE}==================${NC}"

if run_test_suite "E2E" "connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.e2e" "e2e"; then
    test_results["e2e"]="PASSED"
    ((passed_tests++))
else
    test_results["e2e"]="FAILED"
fi
((total_tests++))

# 5. Performance Tests
echo -e "${BLUE}=====================${NC}"
echo -e "${BLUE}5️⃣  PERFORMANCE TESTS${NC}"
echo -e "${BLUE}=====================${NC}"

if run_test_suite "Performance" "connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.performance" "performance"; then
    test_results["performance"]="PASSED"
    ((passed_tests++))
else
    test_results["performance"]="FAILED"
fi
((total_tests++))

# 6. Security Tests
echo -e "${BLUE}==================${NC}"
echo -e "${BLUE}6️⃣  SECURITY TESTS${NC}"
echo -e "${BLUE}==================${NC}"

if run_test_suite "Security" "connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.security" "security"; then
    test_results["security"]="PASSED"
    ((passed_tests++))
else
    test_results["security"]="FAILED"
fi
((total_tests++))

# 7. Accessibility Tests
echo -e "${BLUE}========================${NC}"
echo -e "${BLUE}7️⃣  ACCESSIBILITY TESTS${NC}"
echo -e "${BLUE}========================${NC}"

if run_test_suite "Accessibility" "connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=com.fitnessapp.android.accessibility" "accessibility"; then
    test_results["accessibility"]="PASSED"
    ((passed_tests++))
else
    test_results["accessibility"]="FAILED"
fi
((total_tests++))

# Generate KMobile Report
echo -e "${BLUE}📊 Generating KMobile Performance Report...${NC}"
if command -v kmobile &> /dev/null; then
    kmobile report generate --output "$REPORT_DIR/kmobile/performance_report_$DATE.html"
    kmobile report metrics --output "$REPORT_DIR/kmobile/metrics_$DATE.json"
    echo -e "${GREEN}✅ KMobile reports generated${NC}"
fi

# Generate Consolidated Report
echo -e "${BLUE}📋 Generating Consolidated Test Report...${NC}"

cat > "$REPORT_DIR/test_summary_$DATE.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Fitness App Test Report - $DATE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background: #2196F3; color: white; padding: 20px; border-radius: 8px; }
        .summary { background: #f5f5f5; padding: 15px; margin: 20px 0; border-radius: 8px; }
        .test-suite { margin: 10px 0; padding: 10px; border-left: 4px solid #ddd; }
        .passed { border-left-color: #4CAF50; background: #f8fff8; }
        .failed { border-left-color: #f44336; background: #fff8f8; }
        .stats { display: flex; gap: 20px; margin: 20px 0; }
        .stat { background: white; padding: 15px; border-radius: 8px; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    </style>
</head>
<body>
    <div class="header">
        <h1>🧪 Fitness App Comprehensive Test Report</h1>
        <p>Generated: $DATE</p>
    </div>
    
    <div class="summary">
        <h2>📊 Test Summary</h2>
        <div class="stats">
            <div class="stat">
                <h3>$total_tests</h3>
                <p>Total Test Suites</p>
            </div>
            <div class="stat">
                <h3>$passed_tests</h3>
                <p>Passed Suites</p>
            </div>
            <div class="stat">
                <h3>$((total_tests - passed_tests))</h3>
                <p>Failed Suites</p>
            </div>
            <div class="stat">
                <h3>$((passed_tests * 100 / total_tests))%</h3>
                <p>Success Rate</p>
            </div>
        </div>
    </div>
    
    <div class="test-suites">
        <h2>🔬 Test Suite Results</h2>
EOF

for suite in unit integration ui e2e performance security accessibility; do
    status=${test_results[$suite]}
    class=""
    icon=""
    if [ "$status" = "PASSED" ]; then
        class="passed"
        icon="✅"
    else
        class="failed"
        icon="❌"
    fi
    
    cat >> "$REPORT_DIR/test_summary_$DATE.html" << EOF
        <div class="test-suite $class">
            <h3>$icon $(echo $suite | tr '[:lower:]' '[:upper:]') Tests: $status</h3>
            <p>Report: <a href="$suite/index.html">View Details</a></p>
        </div>
EOF
done

cat >> "$REPORT_DIR/test_summary_$DATE.html" << EOF
    </div>
    
    <div class="kmobile-section">
        <h2>🤖 KMobile Integration</h2>
        <div class="test-suite">
            <h3>📱 Device Testing</h3>
            <p>Performance Report: <a href="kmobile/performance_report_$DATE.html">View Performance</a></p>
            <p>Metrics Data: <a href="kmobile/metrics_$DATE.json">View Metrics</a></p>
        </div>
    </div>
    
    <div class="footer">
        <p><em>Generated by Fitness App Testing Framework with KMobile Integration</em></p>
    </div>
</body>
</html>
EOF

# Final Summary
echo ""
echo -e "${BLUE}=================================================${NC}"
echo -e "${BLUE}📋 COMPREHENSIVE TEST SUMMARY${NC}"
echo -e "${BLUE}=================================================${NC}"

for suite in unit integration ui e2e performance security accessibility; do
    status=${test_results[$suite]}
    if [ "$status" = "PASSED" ]; then
        echo -e "${GREEN}✅ $(printf '%-15s' "$(echo $suite | tr '[:lower:]' '[:upper:]')") : $status${NC}"
    else
        echo -e "${RED}❌ $(printf '%-15s' "$(echo $suite | tr '[:lower:]' '[:upper:]')") : $status${NC}"
    fi
done

echo ""
echo -e "${BLUE}📊 Overall Results:${NC}"
echo -e "   Total Suites: $total_tests"
echo -e "   Passed: $passed_tests"
echo -e "   Failed: $((total_tests - passed_tests))"
echo -e "   Success Rate: $((passed_tests * 100 / total_tests))%"

echo ""
echo -e "${BLUE}📁 Reports Generated:${NC}"
echo -e "   📋 Summary Report: $REPORT_DIR/test_summary_$DATE.html"
echo -e "   📊 Individual Reports: $REPORT_DIR/{unit,integration,ui,e2e,performance,security,accessibility}/"
echo -e "   🤖 KMobile Reports: $REPORT_DIR/kmobile/"

# Cleanup
echo -e "${BLUE}🧹 Cleaning up KMobile environment...${NC}"
if command -v kmobile &> /dev/null; then
    kmobile cleanup
fi

# Exit with appropriate code
if [ $passed_tests -eq $total_tests ]; then
    echo -e "${GREEN}🎉 All tests passed! 🎉${NC}"
    exit 0
else
    echo -e "${RED}💥 Some tests failed. Check the reports for details.${NC}"
    exit 1
fi