package example.grails

import groovy.transform.CompileStatic

@CompileStatic
class MailController {

    EmailService emailService

    static allowedMethods = [send: 'POST']

    def send(Email email) {
        if ( email.hasErrors() ) {
            render status: 422
            return
        }
        log.info(email.toString())
        emailService.send(email)
        render status: 200
    }
}