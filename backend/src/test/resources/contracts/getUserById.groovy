import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        url '/api/v1/users/1'
    }
    response {
        status 200
        headers {
            header('Content-Type': 'application/json')
        }
        body(
            id: $(consumer(regex('[0-9]+')), producer('1')),
            username: $(consumer('testuser'), producer('testuser')),
            email: $(consumer('test@example.com'), producer('test@example.com')),
            firstName: $(consumer('Test'), producer('Test')),
            lastName: $(consumer('User'), producer('User')),
            enabled: $(consumer(true), producer(true))
        )
    }
}