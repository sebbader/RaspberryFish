package com.uni.bonn.semantic.lab.raspberry;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Raspberry {
    private GPIOOutputPin pin;
    private static Raspberry raspberry = null;

    public static Raspberry getInstance() {
        if (raspberry == null) {
            raspberry = new Raspberry();
        }
        return raspberry;
    }

    public Raspberry() {
//        try {
            this.pin = new GPIOOutputPin(RaspiPin.GPIO_23, PinState.LOW);
//        } catch (UnsatisfiedLinkError e) {
//            System.out.println("Failed to init the pin");
//        }

    }

    public void turnPinOn() {
        this.pin.setState(PinState.HIGH);
    }

    public void turnPinOff() {
        this.pin.setState(PinState.LOW);
    }


}
