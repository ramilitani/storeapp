package com.softwareangels.storeapp.batch

import com.softwareangels.storeapp.dto.RegionDTO
import com.softwareangels.storeapp.model.Region
import com.softwareangels.storeapp.repository.ClusterRepository
import com.softwareangels.storeapp.repository.RegionRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RegionsJob(
    val regionRepository: RegionRepository,
    val clusterRepository: ClusterRepository) : BaseJob() {

    val log = LoggerFactory.getLogger(RegionsJob::class.java)

    @Value("\${app.api.regions}")
    val apiHost: String = ""

    var page: Int = 0

    @Scheduled(fixedDelayString = "\${app.scheduler.delay}", initialDelayString = "\${app.scheduler.initial-delay.regions}")
    @Retryable(value = [JobException::class], maxAttemptsExpression = "\${app.retry.max-attempts.regions}",
        backoff = Backoff(delayExpression = "\${app.retry.delay}"))
    fun getRegions() {
        log.info("Running RegionsJob")
        do {
            try {
                val res = httpGet(apiHost, page, Array<RegionDTO>::class.java) as ResponseEntity<Array<RegionDTO>>

                if(res.body?.isEmpty() == true) break

                for (region in res.body!!) {
                    var regionFromDB = regionRepository.findByName(region.name)
                    val cluster = clusterRepository.findByName(region.clusters)
                    regionRepository.save(Region(
                        name = region.name,
                        type = region.type,
                        cluster = cluster!!,
                        id = regionFromDB?.id
                    ))
                }
                page += 1

            } catch (ex: Exception) {
                log.error("Error running RegionsJob on page $page. Error: ${ex.message}")
                throw JobException(message = "Attempt to retry RegionsJob on page $page")
            }
        } while (true)

        log.info("RegionsJob has finished")
    }

    @Recover
    fun recover(exception: JobException) {
        log.warn("Exhausted attempts to run the batch RegionsJob")
    }
}