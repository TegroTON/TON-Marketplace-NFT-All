package money.tegro.market

import org.ktorm.database.Database
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class KtormSetup {
    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    @DependsOnDatabaseInitialization
    fun database(): Database {
        return Database.connectWithSpringSupport(dataSource)
    }
}
