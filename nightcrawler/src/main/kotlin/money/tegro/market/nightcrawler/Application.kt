package money.tegro.market.nightcrawler

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableBatchProcessing
@EntityScan("money.tegro.market.*")
@EnableJpaRepositories("money.tegro.market.*")
@ComponentScan(basePackages = ["money.tegro.market.*"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

