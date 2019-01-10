package com.uni.bonn.semantic.lab.raspberry;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class GpioSingleton {
    private static GpioController single_gpio = null;

    public static GpioController getInstance() {
        if (single_gpio == null) {
//            single_gpio = new String("SingleTon Object");
            single_gpio = GpioFactory.getInstance();
        }
        return single_gpio;
    }
}
