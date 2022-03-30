package com.softwareangels.storeapp

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
@EnableAutoConfiguration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
class StoreAppApplication

fun main(args: Array<String>) {
	runApplication<StoreAppApplication>(*args)
}
