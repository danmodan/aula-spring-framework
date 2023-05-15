package com.danmodan.aulaspringframework.utilities;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Util {

    public static void log(String msg) {

        System.out.println(
            "⏰ " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + 
            " - 🧵 " + Thread.currentThread().getName() +
            " - " + msg
        );
    }

    public static void sleep(long millis) {

        if(millis == 0) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}