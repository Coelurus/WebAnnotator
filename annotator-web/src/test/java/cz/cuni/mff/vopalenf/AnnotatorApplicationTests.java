package cz.cuni.mff.vopalenf;

import cz.cuni.mff.vopalenf.annotator.AnnotatorApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {AnnotatorApplication.class})
class AnnotatorApplicationTests {

    @Test
    void contextLoads() {
        // empty
    }

}
