package com.example.krector.wifip2p;


import android.os.Vibrator;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class HapticService extends WearableListenerService {
    public HapticService() {
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        byte[] data = messageEvent.getData();
        String dataString = new String(data);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern;
        if(dataString.equals("two")) {
            vibrationPattern = new long[]{0, 225, 50, 225};
        }else if(dataString.equals("one")){
            vibrationPattern = new long[]{0, 500};
        }else{
            vibrationPattern = null;
        }
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }
}
