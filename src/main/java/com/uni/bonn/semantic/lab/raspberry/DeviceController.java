package com.uni.bonn.semantic.lab.raspberry;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class DeviceController {
    @GetMapping("/uni-bonn/devices/external")
    public ResponseEntity<String> external_device(HttpServletRequest request, HttpServletResponse response, Model model) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/turtle; charset=utf-8");
        System.out.println("Incoming get request");

        Raspberry raspberry = Raspberry.getInstance();

        String help = "@prefix qudt: <http://qudt.org/schema/qudt#> . " +
                      "@prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> . " +
                      "@prefix unit: <http://data.nasa.gov/qudt/owl/unit#> ." +
                      "<> qudt:QuantityValue {}; qudt:unit unit:DegreeCelsius; a ssn:ObservationValue . ";

        return new ResponseEntity<String>("returning: " + help, responseHeaders, HttpStatus.OK);
    }

    @PutMapping("/uni-bonn/devices/external")
    public ResponseEntity<String> external_device_post(@RequestBody String body, HttpServletRequest request, HttpServletResponse response, Model model) {
        Raspberry raspberry = Raspberry.getInstance();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/turtle; charset=utf-8");
        System.out.println("Incoming put request");
        if (body.toString().contains("TurnOff")) {
            raspberry.turnPinOff();
            return new ResponseEntity<String>("OK", responseHeaders, HttpStatus.OK);
        } else if (body.toString().contains("TurnOn")) {
            raspberry.turnPinOn();
            return new ResponseEntity<String>("OK", responseHeaders, HttpStatus.OK);
        }

        String help = "Bad Request";
        return new ResponseEntity<String>("returning: " + help, responseHeaders, HttpStatus.BAD_REQUEST);
    }
}
