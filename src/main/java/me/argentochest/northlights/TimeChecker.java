package me.argentochest.northlights;

import java.util.Date;

public class TimeChecker {

    private static int lastDay = -1;

    public static boolean newDay(){
        if(lastDay == -1){
            lastDay = new Date().getDay();
            return false;
        }
        if(lastDay != new Date().getDay()){
            lastDay = new Date().getDay();
            return true;
        }
        else return false;
    }

}
