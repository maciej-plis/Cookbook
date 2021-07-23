package matthias.cookbook

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static java.util.UUID.randomUUID

@ActiveProfiles("integration_test")
@AutoConfigureDataMongo
@AutoConfigureMockMvc
@SpringBootTest
class IntegrationTestSpecification extends Specification {

    protected static String sampleId = randomUUID()

    @Autowired
    protected MockMvc mvc
}
