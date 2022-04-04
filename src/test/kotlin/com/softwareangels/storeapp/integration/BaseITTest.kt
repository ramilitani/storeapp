package com.softwareangels.storeapp.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate

open class BaseITTest {
    @Autowired
    protected lateinit var restTemplate: TestRestTemplate
}