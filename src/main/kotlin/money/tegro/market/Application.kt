package money.tegro.market

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@Configuration
@ConfigurationPropertiesScan("money.tegro.market.properties")
@EnableJdbcRepositories("money.tegro.market.repository")
@EnableRabbit
@SpringBootApplication
//@DependsOnDatabaseInitialization
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
