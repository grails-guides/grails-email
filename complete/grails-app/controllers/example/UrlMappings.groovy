package example

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
            }
        }

        //tag::urlmapping[]
        post "/mail/send"(controller: 'mail', action: 'send')
        //end::urlmapping[]

        "/"(controller: 'application', action: 'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
