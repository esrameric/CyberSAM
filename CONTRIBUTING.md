# Contributing to CyberSAM

Thank you for your interest in contributing to CyberSAM - Secure Software Asset Management!

## Code of Conduct

We are committed to providing a welcoming and inspiring community for all. Please read and abide by our Code of Conduct.

## How to Contribute

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* **Use a clear and descriptive title**
* **Describe the exact steps which reproduce the problem**
* **Provide specific examples to demonstrate the steps**
* **Describe the behavior you observed after following the steps**
* **Explain which behavior you expected to see instead and why**
* **Include screenshots and animated GIFs if possible**
* **Include your Java/Node version and OS**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **Use a clear and descriptive title**
* **Provide a step-by-step description of the suggested enhancement**
* **Provide specific examples to demonstrate the steps**
* **Describe the current behavior and expected behavior**
* **Explain why this enhancement would be useful**

### Pull Requests

* Fill in the required template
* Follow the TypeScript and Java style guides
* Include appropriate test cases
* Update documentation as needed
* End all files with a newline

## Development Setup

### Backend Development

```bash
cd backend
mvn clean install
mvn spring-boot:run

# Tests
mvn test

# Code quality
mvn checkstyle:check
```

### Frontend Development

```bash
cd frontend
npm install
npm run dev

# Linting
npm run lint

# Type checking
npm run type-check
```

## Code Style

### Java (Backend)

* 4 spaces for indentation
* Follow Google Java Style Guide
* Use meaningful variable names
* Add JSDoc comments for public methods
* Use `@RequiredArgsConstructor` from Lombok
* Keep methods focused and small

Example:
```java
/**
 * Find all software assets with expired licenses.
 *
 * @return List of expired software assets
 */
@Query("SELECT sa FROM SoftwareAsset sa WHERE sa.expiryDate < CURRENT_DATE")
List<SoftwareAsset> findExpiredLicenses();
```

### TypeScript/React (Frontend)

* 2 spaces for indentation
* Use functional components
* Use React hooks
* Add JSDoc comments for components
* Use TypeScript strict mode
* Meaningful component and function names

Example:
```typescript
/**
 * DashboardCard Component
 * Displays key metrics for the defense industry dashboard
 */
export const DashboardCard: React.FC<DashboardCardProps> = ({
  title,
  value,
  icon,
}) => {
  // implementation
};
```

## Commit Messages

* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally after the first line

Example:
```
Add asset vulnerability filtering

- Implement new filter API endpoint
- Add filtering UI component
- Update dashboard to use filter
- Fixes #123
```

## Testing

### Backend Tests

```bash
cd backend

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SoftwareAssetControllerTest

# With coverage
mvn test jacoco:report
```

### Frontend Tests

```bash
cd frontend

# Run all tests
npm test

# Run with coverage
npm test -- --coverage

# Watch mode
npm test -- --watch
```

## Documentation

* Update README files if changing functionality
* Add comments for complex logic
* Include examples in documentation
* Keep documentation up to date

## Additional Notes

### Issue and Pull Request Labels

* `bug` - Something isn't working
* `enhancement` - New feature or request
* `documentation` - Improvements or additions to documentation
* `good first issue` - Good for newcomers
* `help wanted` - Extra attention is needed
* `question` - Further information is requested

### Project Maintainers

The CyberSAM project is maintained by [Your Organization]. Please reach out to the maintainers for any questions.

---

Thank you for contributing to making CyberSAM better!
