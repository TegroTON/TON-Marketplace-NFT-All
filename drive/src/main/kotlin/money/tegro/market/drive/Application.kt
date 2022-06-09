package money.tegro.market.drive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class DriveApplication

fun main(args: Array<String>) {
    runApplication<DriveApplication>(*args)
}

@RestController
@RequestMapping("/api/v1")
class APIv1Controller {
    @GetMapping("/collection")
    suspend fun getAllCollections() = "peepeepoopoo"
}
