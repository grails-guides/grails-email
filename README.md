# grails-email

Sample app for **Sending Email from a Grails 8 Application (and Testing It)** (Apache Grails `8.0.0-SNAPSHOT`, JDK 21).

The guide shows how to send mail from a Grails 8 service over SMTP (Grails Mail plugin) and through a provider (SendGrid / AWS SES), how to inject and externalize mail configuration, and how to verify the send interaction using Spock Spring integration tests — without dispatching real email.

## Layout

| Directory | What it is |
|-----------|------------|
| `initial/` | Grails 8 REST API starter from [start.grails.org](https://start.grails.org) (`views-json`, `spock`). Work through the guide starting here. |
| `complete/` | Finished sample — `EmailService` boundary, SMTP / SendGrid / SES implementations, `POST /mail/send`, and a Spock Spring integration spec that mocks the sender. |

## Running

```bash
git clone -b grails8 https://github.com/grails-guides/grails-email.git
cd grails-email/complete
./gradlew test integrationTest
```

To follow the guide step by step, start from `initial/`:

```bash
cd grails-email/initial
./gradlew test
```

Run the finished app locally (SMTP defaults to `localhost:1025` — use [MailHog](https://github.com/mailhog/MailHog) or similar for manual verification):

```bash
cd grails-email/complete
./gradlew bootRun
```

Example endpoint:

```bash
curl -X POST http://localhost:8080/mail/send \
  -H 'Content-Type: application/json' \
  -d '{"recipient":"user@example.com","subject":"Hello","textBody":"Hi there"}'
```

Switch providers via `application.yml` or environment variables:

| Provider | Set |
|----------|-----|
| SMTP (default) | `EMAIL_PROVIDER=smtp` plus `SMTP_HOST`, `SMTP_PORT`, etc. |
| SendGrid | `EMAIL_PROVIDER=sendgrid`, `SENDGRID_APIKEY`, `SENDGRID_FROM_EMAIL` |
| AWS SES | `EMAIL_PROVIDER=ses`, `AWS_REGION`, `AWS_SOURCE` (+ standard AWS credentials) |

The integration spec (`MailControllerSpec`) uses a Spock mock for `EmailService` and asserts the message is built and the sender is invoked — no live email is sent in CI.

## Requirements

- **JDK 21** (Temurin recommended; the Gradle build enforces Java 21+)
- Optional: local SMTP catcher on port **1025** for manual `bootRun` verification

If Gradle reports *"Run this build using a Java 21 or newer JVM"*, switch your shell to JDK 21:

```bash
sdk install java 21.0.6-tem
sdk default java 21.0.6-tem
java -version   # should show 21.x
```

## The pattern, in one place

```yaml
# application.yml — pick a provider and externalize secrets
email:
  provider: ${EMAIL_PROVIDER:smtp}

grails:
  mail:
    host: ${SMTP_HOST:localhost}
    port: ${SMTP_PORT:1025}
    default:
      from: ${SMTP_FROM:no-reply@example.com}
```

```groovy
// grails-app/conf/spring/resources.groovy — inject the right implementation
beans = {
    def provider = application.config.getProperty('email.provider', String, 'smtp')
    switch (provider) {
        case 'sendgrid': emailService(SendGridEmailService); break
        case 'ses':      emailService(AwsSesMailService); break
        default:         emailService(SmtpEmailService)
    }
}
```

Grails 8 uses `org.grails.plugins:grails-mail` 5.x from the Grails plugin repository — compatible with Apache Grails 8.

## Guide prose

Published narrative lives on [grails.apache.org/guides](https://grails.apache.org/guides/) in [apache/grails-static-website](https://github.com/apache/grails-static-website) under `guides/grails-email/v8/`.

## CI

GitHub Actions (`.github/workflows/grails8.yml`) runs `./gradlew test` for `initial` and `complete`, and `./gradlew integrationTest` for `complete`, on pushes and PRs to the `grails8` branch.
