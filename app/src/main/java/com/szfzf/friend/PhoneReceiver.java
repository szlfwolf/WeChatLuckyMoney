package com.szfzf.friend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.app.Service;
import android.util.Log;

import com.szfzf.friend.services.FriendService;
import com.szfzf.friend.utils.AppUtil;


/**
 * Created by szlfw on 2017/7/7.
 */


public class PhoneReceiver extends BroadcastReceiver {

    private static final String TAG = "message";
    private static boolean mIncomingFlag = false;
    private static String mIncomingNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果是拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            mIncomingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i(TAG, "call OUT:" + phoneNumber);

        } else {
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()) {

                case TelephonyManager.CALL_STATE_RINGING:
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    Log.i(TAG, "RINGING :" + mIncomingNumber);
                    if(mIncomingNumber!=null&&mIncomingNumber.endsWith("3877")){
                        AppUtil.openCLD("com.tencent.mm", context);
                        FriendService.instance.setServiceEnable();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mIncomingFlag) {
                        Log.i(TAG, "incoming ACCEPT :" + mIncomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mIncomingFlag) {
                        Log.i(TAG, "incoming IDLE");
                    }
                    break;
            }
        }
    }

}
