# .github/branch-protection.yml
# This file documents the branch protection rules for GitHub
# Actual branch protection rules are configured in the GitHub repository settings

## Branch Protection Rules

### Main Branch (production)
- Require pull request reviews before merging
  - Required number of reviewers: 2
  - Required review from code owners
  - Allow specified actors to bypass required reviews
- Require status checks to pass before merging
  - Required status checks from GitHub Actions:
    - test-backend
    - test-frontend
    - security-scan
    - build-and-push
- Require branches to be up to date before merging
- Require conversation resolution before merging
- Restrict who can push to matching branches
  - Allow force pushes: false
  - Allow deletions: false

### Develop Branch (staging)
- Require pull request reviews before merging
  - Required number of reviewers: 1
- Require status checks to pass before merging
  - Required status checks from GitHub Actions:
    - test-backend
    - test-frontend
    - security-scan
- Restrict who can push to matching branches

## Code Review Requirements

### Review Process
1. All code changes must go through pull request review
2. At least one team member must approve the PR before merging
3. For main branch: Two team members must approve
4. Code owners must review changes to their areas

### Code Review Checklist
- [ ] Code follows project coding standards
- [ ] Tests are included and passing
- [ ] Security implications are considered
- [ ] Performance implications are addressed
- [ ] Documentation is updated if needed
- [ ] Changes are properly reviewed for bugs
- [ ] Code is maintainable and readable
- [ ] All CI checks are passing

## Pull Request Template
Use the provided pull request template that includes:
- Summary of changes
- Issue references
- Testing performed
- Risks and mitigations
- Breaking changes (if any)

## Automated Code Quality Checks
- ESLint for frontend code
- Checkstyle for backend code
- Security scanning using OWASP Dependency Check
- Code coverage threshold (80% minimum)
- Automated formatting with Prettier and Spotless