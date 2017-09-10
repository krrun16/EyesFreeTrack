package com.example.krector.wifip2p;


import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class HapticService extends WearableListenerService {
    static long[] vibrationTwo = new long[]{0, 225, 50, 225};
    static long[] vibrationOne = new long[]{0, 500};
    Vibrator vibrator;
    public HapticService() {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("message", "" + System.currentTimeMillis());
        super.onMessageReceived(messageEvent);

        if (vibrator == null) vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        byte[] data = messageEvent.getData();
        String dataString = new String(data);
        long[] vibrationPattern;
        if(dataString.equals("two")) {
            vibrationPattern = vibrationTwo;
        }else if(dataString.equals("one")){
            vibrationPattern = vibrationOne;
        }else{
            vibrationPattern = null;
        }
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        Log.d("vibrate", "" + System.currentTimeMillis());
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }
}
