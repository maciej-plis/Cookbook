package matthias.cookbook

import matthias.cookbook.common.ExceptionResponseHandler
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static java.util.UUID.randomUUID

class TestSpecification extends Specification {

    protected static String sampleId = randomUUID()
    protected static MockMvc mvc

    protected static void setupMvc(Object... controllers) {
        mvc = MockMvcBuilders.standaloneSetup(controllers)
                .setControllerAdvice(new ExceptionResponseHandler())
                .build()
    }
}
