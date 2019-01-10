package com.uni.bonn.semantic.lab.raspberry;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class GPIOOutputPin {
    Pin id;
    GpioController gpio;
    GpioPinDigitalOutput pin;


    public GPIOOutputPin(Pin id, PinState defaultState) {
        this.id = id;
        this.gpio = GpioSingleton.getInstance();
        this.pin = gpio.provisionDigitalOutputPin(this.id, defaultState);
    }

    public PinState state() {
        return this.pin.getState();
    }

    public void setState(PinState state) {
        this.pin.setState(state);
    }

}
