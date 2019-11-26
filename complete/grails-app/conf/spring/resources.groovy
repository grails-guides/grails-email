import example.grails.AwsSesMailService
import example.grails.SendGridEmailService

beans = {
    if ( System.getProperty('SENDGRID_FROM_EMAIL') && System.getProperty('SENDGRID_APIKEY') ) {
        emailService(SendGridEmailService)
    } else {
        emailService(AwsSesMailService) {
            awsCredentialsProviderService = ref('awsCredentialsProviderService')
        }
    }
}
