package com.softwareangels.storeapp.batch

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = arrayOf("app.scheduler.enabled"), matchIfMissing = true)
class SchedulerConfig {

}