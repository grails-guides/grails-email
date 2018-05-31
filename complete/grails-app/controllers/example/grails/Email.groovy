package example.grails

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import groovy.transform.ToString

@ToString
@GrailsCompileStatic
class Email implements Validateable {
    String recipient
    List<String> cc = []
    List<String> bcc = []
    String subject
    String htmlBody
    String textBody
    String replyTo

    static constraints = {
        recipient nullable: false
        subject nullable: false
        htmlBody nullable: true
        textBody nullable: true, validator: { String val, Email obj ->
            !(!obj.htmlBody && !val)
        }
        replyTo nullable: true
    }
}
