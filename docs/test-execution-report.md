# Booking Management — Test Execution Report

**Run date:** 2026-10-26
**Environment:** https://eventhub.rahulshettyacademy.com (API: https://api.eventhub.rahulshettyacademy.com/api)
**Browser:** Chromium / Chrome 155 (real browser)
**Account:** `rahulshetty1@gmail.com` (shared live account, so the suites run one after another, never in parallel)

## Summary

| Suite | Framework | Tests | Passed | Failed | Skipped | Duration |
|---|---|---|---|---|---|---|
| `tests/booking-flow.spec.js` + `tests/booking-management.spec.js` | Playwright 1.58.2 (Node 24.22.0) | 7 | 7 | 0 | 0 | 1.3 min |
| `selenium/` — `BookingFlowTest` | Selenium 4 + TestNG (Java 25, Maven 3.9.9) | 3 | 3 | 0 | 0 | ~8.6 s (test bodies) |
| **Total** | | **10** | **10** | **0** | **0** | |

**Result: PASS**

## Playwright

Command:
```bash
npx playwright test tests/booking-flow.spec.js tests/booking-management.spec.js --reporter=line,html
```

| ID | Test | Spec | Result |
|---|---|---|---|
| TC-001 | Bookings list shows a card with the booking details | booking-flow.spec.js | ✅ Pass |
| TC-002 | Booking detail page shows all sections | booking-flow.spec.js | ✅ Pass |
| TC-003 | Cancelling from the detail page shows a toast, redirects and deletes the booking | booking-flow.spec.js | ✅ Pass |
| TC-004 | Cancelling from a list card removes only that card and stays on the list | booking-flow.spec.js | ✅ Pass |
| TC-005 | Clear all bookings empties the list after confirming | booking-flow.spec.js | ✅ Pass |
| TC-006 | Booking through the UI then "View My Bookings" lists the new booking first | booking-flow.spec.js | ✅ Pass |
| TC-107 | Booking reference starts with the first letter of the event title (uppercase) | booking-management.spec.js | ✅ Pass |

Observed in this run: all test data was booked on "Dilli Diwali Mela". TC-107 got ref `D-9N3TVO`, which satisfies the booking-ref rule. TC-006 booked `D-UMRNNB` through the UI.

HTML report: `playwright-report/index.html` (`npx playwright show-report`)

## Selenium (Java)

Command:
```bash
mvn -f selenium/pom.xml clean test
```

| ID | Test method | Result | Duration |
|---|---|---|---|
| TC-001 | `tc001_bookingsListShowsCard`: bookings list shows a card with the booking details | ✅ Pass | 1.7 s |
| TC-002 | `tc002_detailPageShowsAllSections`: booking detail page shows all sections | ✅ Pass | 3.2 s |
| TC-003 | `tc003_cancelFromDetailPage`: cancelling from the detail page shows a toast, redirects and deletes the booking | ✅ Pass | 3.7 s |

Reports: `selenium/target/surefire-reports/index.html`, `emailable-report.html`, `testng-results.xml`

Non-blocking warnings: Selenium printed `Unable to find CDP implementation matching 155`. That only affects DevTools features, which these tests don't use. To silence it, add the matching `selenium-devtools` dependency or upgrade Selenium.

## Coverage vs. scenarios

`docs/test-strategy.md` §2.4 assigns 10 scenarios to the E2E layer. These suites cover the six booking-journey ones (TC-001 to TC-006). Playwright covers all six plus TC-107 (booking-ref prefix, which also has unit and API layers in the strategy). Selenium covers TC-001 to TC-003.

Not covered by this run: the remaining E2E-layer scenarios in the strategy, such as TC-200 (cross-user "Access Denied") and TC-512 (list/seat refresh). They have no dedicated test in these suites.

Test data cleanup: each test creates its own booking through the API and deletes it afterwards (TC-005 clears the account's bookings by design).
