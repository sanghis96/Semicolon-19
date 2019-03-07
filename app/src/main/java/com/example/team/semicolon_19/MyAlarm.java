package com.example.team.semicolon_19;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Belal on 8/29/2017.
 */

//class extending the Broadcast Receiver
public class MyAlarm extends BroadcastReceiver {

    //the method will be fired when the alarm is triggerred
    @Override
    public void onReceive(Context context, Intent intent) {

        //you can check the log that it is fired
        //Here we are actually not doing anything
        //but you can do any task here that you want to be done at a specific time everyday
        Intent i = new Intent(context,InstructionsActivity.class);
        context.startActivity(i);
        Log.d("MyAlarmBelal", "Alarm just fired");
    }

}