package com.uni.bonn.semantic.lab.raspberry;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


//@Controller
//public class HomeController {
//    @RequestMapping(value="/")
//    public String home() {
//        return "test";
//    }
//}


@Controller
public class GreetingController {
    @GetMapping("/greeting")
    public String greeting(
            @RequestParam(name="name", required=false, defaultValue="World"
        ) String name, Model model) {

        model.addAttribute("name", name);
        return "greeting";

    }

}