# Runtime Credentials

The following properties should only be passed at test execution time and **must never be committed to the repository**. They contain credentials.

```properties
# Password used to encrypt and decrypt test data at runtime
taf.testDataEncryptionPassword=<your test data password>

# Auth token of the technical Jira user, required for communication with Jira/Xray
auth_token=<your Jira auth token>

# LDAP or technical username used as a fallback for Jira communication
# and to determine the assignee
username=<technical username>

# Password of the technical user used as a fallback for Jira communication
password=<technical user password>
```

## Example CLI Usage

```bash
mvn clean test \
  -Dtaf.testDataEncryptionPassword=<your test data password> \
  -Dauth_token=<your Jira auth token> \
  -Dusername=<technical username> \
  -Dpassword=<technical user password>
```

## CI Recommendations

- Pass each property through a CI secret (GitHub Actions, GitLab CI variables, etc.) — never inline them in workflow files.
- Mask the values in CI logs.
- Rotate the technical user's Jira auth token periodically; ATAF picks the value up at every test run.
- Keep `taf.testDataEncryptionPassword` consistent across environments that share the same encrypted test data set.
