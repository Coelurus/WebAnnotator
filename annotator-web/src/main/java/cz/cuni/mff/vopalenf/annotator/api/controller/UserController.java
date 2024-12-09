package cz.cuni.mff.vopalenf.annotator.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @PostMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/login")
    public String login() {
        return "login";
    }

}
