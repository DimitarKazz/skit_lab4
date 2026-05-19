# SKIT Lab 4 — Automated Tests

## Part 1 — Selenium UI Tests (OrangeHRM)

Automated UI tests for the **Add User** and **Delete User** functionalities on [OrangeHRM Demo](https://opensource-demo.orangehrmlive.com).

### Structure
```
src/test/java/com/example/selenium/
├── base/BaseTest.java              # WebDriver setup/teardown
├── pages/
│   ├── LoginPage.java              # Page Object for Login
│   └── UserManagementPage.java     # Page Object for User Management
└── tests/
    ├── AddUserTest.java            # Add user scenarios
    └── DeleteUserTest.java         # Delete user scenarios
```

### Test Scenarios

**Add User:**
- Add ESS user successfully
- Add Admin user successfully
- Add user with Disabled status
- Validation errors on empty form
- Added user appears in list

**Delete User:**
- Delete existing user successfully
- Deleted user not searchable
- Delete multiple users

---

## Part 2 — JUnit + Mockito (CourseService)

Unit tests for `CourseService` using Mockito mocks.

### Test Scenarios

**findActiveCourses()** — 4 tests  
**findById()** — 2 tests  
**createCourse()** — 7 tests  
**deleteCourse()** — 4 tests  

---

## How to Run

```bash
mvn test
```

> For Selenium tests, Chrome must be installed. WebDriverManager handles the driver automatically.
