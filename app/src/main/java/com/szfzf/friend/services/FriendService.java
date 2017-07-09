package com.szfzf.friend.services
        ;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by szlfw on 2017/7/7.
 * 获取到短信通知
 *  0.唤醒屏幕
 *  1.打开微信
 *  2.确保当前页是主页界面
 *  3.找到“工作”tab并且点击
 *  4.确保到达签到页面
 *  5.找到签到按钮，并且点击
 *  6.判断签到是否成功
 *      1.成功，退出程序
 *      2.失败，返回到主页，重新从1开始签到
 */

public class FriendService extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {
    private String TAG = getClass().getSimpleName();

    private  boolean  isFinish = false;

    public static FriendService instance;
    private int index = 1;
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
    @Override
    public void onInterrupt() {

    }
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                addFriend(event);
                break;
        }

    }
    private void addFriend(AccessibilityEvent event) {
        ArrayList<String> texts = new ArrayList<String>();
        Log.i(TAG, "事件---->" + event.getEventType());


        if(isFinish){
            return;
        }

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return ;
        }
        System.out.println("index:"+index);
        switch (index) {

            case 1: //进入主页
                OpenHome(event.getEventType(),nodeInfo);
                break;
            case 2: //进入功能列表【+】
                OpenMoreFunc(event.getEventType(),nodeInfo);
                break;
            case 3://进入添加朋友？
                OpenSearchFriendBar(event.getEventType(),nodeInfo);
                break;
            case 4://进入搜索朋友
                FillSearchFriendBar(event.getEventType(),nodeInfo);
                break;

            default:
                break;
        }

    }

    private ArrayList<String> getTextList(AccessibilityNodeInfo node,ArrayList<String> textList){
        if(node == null) {
            Log.w(TAG, "rootWindow为空");
            return null;
        }
        if(textList==null){
            textList = new ArrayList<String>();
        }
        String text = node.getText().toString();
        if(text!=null&&text.equals("")){
            textList.add(text);
        }
//        node.get
        return null;

    }


    private void OpenHome(int type,AccessibilityNodeInfo nodeInfo) {
        if(type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            //判断当前是否是微信主页
            List<AccessibilityNodeInfo> nodeList = nodeInfo.findAccessibilityNodeInfosByText("搜索");
            if(!nodeList.isEmpty()){
                //点击
                boolean isHome = onclick( nodeList.get(0).getParent().getChild(7));
                System.out.println("---->"+isHome);
                index = 2;
                System.out.println("已点击【+】");
            }
        }

    }


    private void OpenMoreFunc(int type,AccessibilityNodeInfo nodeInfo) {
        if(type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            //判断当前是否是主页的签到页
            List<AccessibilityNodeInfo> nodeList = nodeInfo.findAccessibilityNodeInfosByText("添加朋友");
            if(!nodeList.isEmpty()){
                boolean ret = click( "添加朋友");
                System.out.println("---->"+ret);
                index = 3;
                System.out.println("点击进入【添加朋友】");
            }
        }
    }

    private OutputStream os;
    private final boolean exec(String cmd) {
        try {
            if (os == null) {
                os = Runtime.getRuntime().exec("su").getOutputStream();
            }
            os.write(cmd.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"exec",e);
            return  false;
        }
        return true;
    }

    private void OpenSearchFriendBar(int type,AccessibilityNodeInfo nodeInfo) {
        if(type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            //判断当前是否是主页的签到页
            List<AccessibilityNodeInfo> nodeList = nodeInfo.findAccessibilityNodeInfosByText("微信号/QQ号/手机号");
            if(!nodeList.isEmpty()){

                boolean ret = exec("input tap 108 377 \n");

                //boolean ret = click("微信号/QQ号/手机号");
                System.out.println("---->"+ret);
                index = 4;
                System.out.println("点击进入【搜索】");
            }
        }
    }

    private void FillSearchFriendBar(int type,AccessibilityNodeInfo nodeInfo) {
        if(type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            //判断当前是否是主页的签到页
            List<AccessibilityNodeInfo> nodeList = nodeInfo.findAccessibilityNodeInfosByText("?");
            if(!nodeList.isEmpty()){
                AccessibilityNodeInfo nodeToInput = nodeList.get(0).getParent().getChild(1);
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo
                        .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "13511112222");
                nodeToInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                index = 5;
                System.out.println("点击进入【添加朋友】详情3");
            }

//           index = ret?3:1;
        }

    }


    private void doQianDao(int type,AccessibilityNodeInfo nodeInfo) {
        if(type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            //判断当前页是否是签到页
            List<AccessibilityNodeInfo> case1 = nodeInfo.findAccessibilityNodeInfosByText("开启我的签到之旅");
            if(!case1.isEmpty()){
                click("开启我的签到之旅");
                System.out.println("点击签到之旅");
            }

            List<AccessibilityNodeInfo> case2 = nodeInfo.findAccessibilityNodeInfosByText("我知道了");
            if(!case2.isEmpty()){
                click("我知道了");
                System.out.println("点击我知道对话框");
            }
            List<AccessibilityNodeInfo> case3 = nodeInfo.findAccessibilityNodeInfosByText("签到");
            if(!case3.isEmpty()){
                Toast.makeText(getApplicationContext(), "发现目标啦！！~~", Toast.LENGTH_LONG).show();
                System.out.println("发现目标啦！");
                click("签到");
                isFinish = true;
            }
        }

//      if(type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
//          List<AccessibilityNodeInfo> case3 = nodeInfo.findAccessibilityNodeInfosByText("签到");
//          if(!case3.isEmpty()){
//              Toast.makeText(getApplicationContext(), "发现目标啦！！~~", 1).show();
//          }
//      }

    }


    //通过文字点击
    private boolean click(String viewText){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "点击失败，rootWindow为空");
            return false;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(viewText);
        if(list.isEmpty()){
            //没有该文字的控件
            Log.w(TAG, "点击失败，"+viewText+"控件列表为空");
            return false;
        }else{
            //有该控件
            //找到可点击的父控件
            AccessibilityNodeInfo view = list.get(0);
            return onclick(view);  //遍历点击
        }

    }

    private boolean onclick(AccessibilityNodeInfo view){
        if(view.isClickable()){
            view.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.w(TAG, "点击成功");
            return true;
        }else{

            AccessibilityNodeInfo parent = view.getParent();
            if(parent==null){
                return false;
            }
            return onclick(parent);
        }

    }

    //点击返回按钮事件
    private void back(){
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }


    @Override
    protected void onServiceConnected() {
        // TODO Auto-generated method stub
        super.onServiceConnected();
        Log.i(TAG, "service connected!");
        Toast.makeText(getApplicationContext(), "连接成功！", Toast.LENGTH_LONG).show();
        instance = this;
    }

    public void setServiceEnable(){
        isFinish = false;
        Toast.makeText(getApplicationContext(), "服务可用开启！", Toast.LENGTH_LONG).show();
        index = 1;
    }


}
