package cz.cuni.mff.vopalenf.annotator.controllers;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import cz.cuni.mff.vopalenf.annotator.Greeting;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class GreetingController {

    @PostMapping("/greet")
    public String greet(@ModelAttribute Greeting values) {
        return "Hello " + values.getValue() + "!";
    }

    @GetMapping("/user")
    public String user(){
        return "Kiki";
    }

    @GetMapping(path = "/test")
    public String test() {
        return "<div id=\"hello\" hx-swap-oob=\"true\">Hello</div>\n"
                + "<div id=\"world\" hx-swap-oob=\"true\">World</div>";
    }

}