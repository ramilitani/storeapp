package com.softwareangels.storeapp.batch

import com.softwareangels.storeapp.dto.StoreDTO
import com.softwareangels.storeapp.model.Store
import com.softwareangels.storeapp.repository.RegionRepository
import com.softwareangels.storeapp.repository.StoreRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class StoresJob(
    val storeRepository: StoreRepository,
    val regionRepository: RegionRepository) : BaseJob() {

    val log = LoggerFactory.getLogger(StoresJob::class.java)

    @Value("\${app.api.stores}")
    val apiHost: String = ""

    var page: Int = 0

    @Scheduled(fixedDelayString = "\${app.scheduler.delay}", initialDelayString = "\${app.scheduler.initial-delay.stores}")
    @Retryable(value = [JobException::class], maxAttemptsExpression = "\${app.retry.max-attempts.stores}",
        backoff = Backoff(delayExpression = "\${app.retry.delay}"))
    fun getStores() {
        log.info("Running StoresJob")
        do {
            try {
                val res = httpGet(apiHost, page, Array<StoreDTO>::class.java) as ResponseEntity<Array<StoreDTO>>

                if(res.body?.isEmpty() == true) break

                for (store in res.body!!) {
                    var storeFromDB = storeRepository.findByNameAndRegionName(store.name, store.region)
                    val region = regionRepository.findByName(store.region)
                    storeRepository.save(
                        Store(
                            name = store.name,
                            theme = store.theme,
                            region = region!!,
                            id = storeFromDB?.id)
                    )
                }
                page += 1

            } catch (ex: Exception) {
                log.error("Error running StoresJob on page $page. Error: ${ex.message}")
                throw JobException(message = "Attempt to retry StoresJob on page $page")
            }
        } while (true)

        log.info("StoresJob has finished")
    }

    @Recover
    fun recover(exception: JobException) {
        log.warn("Exhausted attempts to run the batch StoresJob")
    }
}