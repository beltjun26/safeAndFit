package com.upv.rosiebelt.safefit.utility;

import android.util.Log;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class AccidentDetection {

    boolean accidentInitiated = false, hasDropped = false, confirmedAccident = false;
    Calendar startTime, endTime;
    int threshold = 12, stillTime = 30;
    Queue<float[]> accHist;
    private float[] accEarth;
    public AccidentDetection() {
        accHist = new LinkedList();
        accEarth = new float[3];
    }

    public void updateData(float[] newData){
        if(accHist.size() > 8){
            accHist.poll();
        }
        accHist.add(newData);
        accEarth = newData;
    }

    public boolean checkAccident(){
        if(accidentInitiated){
            Log.e("Accident", "initiated");
            if(hasDropped){
                Log.e("Accident", "dropped");
                if(confirmedAccident){
                    Log.e("Accident", "accident");
                    accidentInitiated = false;
                    hasDropped = false;
                    confirmedAccident = false;
                    return true;
                }else {
                    checkStill();
                    Log.e("Accident", "still");
                }
            }else {
                checkFloorDrop();
            }
        }else{
            checkAccidentInitiation();
        }
        return false;
    }

    public void checkAccidentInitiation(){
        if(accEarth[0] > threshold || accEarth[0] < (threshold*-1) || accEarth[1] > threshold || accEarth[1] < (threshold*-1)){
            accidentInitiated = true;
            startTime = Calendar.getInstance();
            startTime.add(Calendar.SECOND, 1);
        }
    }

    public void checkFloorDrop(){
        endTime = Calendar.getInstance();
        long difference = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        if(difference < 1500 && difference > 0){
            if(accEarth[1] < (threshold-5)*-1){
                hasDropped = true;
                startTime = Calendar.getInstance();
                startTime.add(Calendar.SECOND, 3);
            }
        }else if(difference > 1500){
            accidentInitiated = false;
        }

    }


    public void checkStill(){
        endTime = Calendar.getInstance();
        long difference = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        if(difference < stillTime*1000 && difference >0 ){
            boolean moved = false;
            if(accEarth[0]> 1 || accEarth[0]< -1){
                moved = true;
            }else if(accEarth[1]> 1 || accEarth[1]< -1){
                moved = true;
            }else if(accEarth[2]> 1 || accEarth[2]< -1){
                moved = true;
            }
            if(moved){
                accidentInitiated = false;
                hasDropped = false;
            }
        }else if(difference > stillTime*1000){
            confirmedAccident = true;
        }

    }


    public void setThreshold(int threshold){
        this.threshold = threshold;
    }

}
