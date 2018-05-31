package example.grails

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@Integration
class MailControllerSpec extends Specification {

    @Autowired
    MailController mailController

    @Qualifier('mockEmailService')
    @Autowired
    EmailService emailService

    def "/mail/send interacts once email service"() {
        given:
        RestBuilder rest = new RestBuilder()

        mailController.emailService = emailService

        when:
        RestResponse resp = rest.post("http://localhost:${serverPort}/mail/send") {
            accept('application/json')
            contentType('application/json')
            json {
                subject = 'Test'
                recipient = 'delamos@grails.example'
                textBody  = 'Hola hola'
            }
        }

        then:
        resp.status == 200
        1 * emailService.send(_)

    }

    @TestConfiguration
    static class EmailServiceConfiguration {
        private DetachedMockFactory factory = new DetachedMockFactory()

        @Qualifier('mockEmailService')
        @Bean
        EmailService emailService() {
            factory.Mock(EmailService)
        }
    }
}
