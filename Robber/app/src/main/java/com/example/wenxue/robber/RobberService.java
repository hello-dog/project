package com.example.wenxue.robber;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RobberService extends AccessibilityService {

    private AccessibilityNodeInfo mAccessibilityNodeInfo = null;
    List<AccessibilityNodeInfo> list = null;

    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();
        list = new ArrayList<>();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED){
            Log.d("RobberService", "--------------: notification");
            List<CharSequence> listText = event.getText();
            if (!listText.isEmpty()) {
                for(CharSequence cS : listText){
                    String text = String.valueOf(cS);
                    Log.d("RobberService", "list text:  " + text);
                    if (text.contains("[微信红包]")) {
                        if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
                            break;
                        }
                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            mAccessibilityNodeInfo = event.getSource();
            if (mAccessibilityNodeInfo == null) {
                return;
            }
//            Log.d("RobberService", "-----------content ----------------------:  " + mAccessibilityNodeInfo.getClassName());
//            Log.d("RobberService", "id: " + mAccessibilityNodeInfo.toString());
//            Log.d("RobberService", "children: " + mAccessibilityNodeInfo.getChildCount());
//            Log.d("RobberService", "text: " + mAccessibilityNodeInfo.getText());
//            Log.d("RobberService", "is clickable: " + mAccessibilityNodeInfo.isClickable());
//            getInfo(mAccessibilityNodeInfo, "");

            List<AccessibilityNodeInfo> listNodes = mAccessibilityNodeInfo.findAccessibilityNodeInfosByText("微信红包");
            if (listNodes.size() > 0) {
                //Log.d("RobberService", "--------list.size: " + listNodes.size());
                list.clear();
                int count = getNodes(mAccessibilityNodeInfo);
               // Log.d("RobberService", "--------node count: " + count);
                int index1 = getIndex("微信红包");
                int index2 = getIndex("你领取了");
                Log.d("RobberService", "--------index1: " + index1 + " index2: " + index2);
//                if (index1 != -1){
//                    AccessibilityNodeInfo p = list.get(index1).getParent();
//                    if(p != null){
//                        Log.d("RobberService", "--------set");
//
//                    }
//                }
                if (index1 < index2){
                    return;
                }
                AccessibilityNodeInfo currentNodeInfo = listNodes.get(listNodes.size() - 1);
                if (currentNodeInfo != null && currentNodeInfo.getParent().isClickable()) {
                    Log.d("RobberService", "--------click" + currentNodeInfo.getText());
                    currentNodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            } else {
                return;
            }

        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            String className = event.getClassName().toString();
            Log.d("RobberService", "---------state----------------------: " + className);
            AccessibilityNodeInfo info = event.getSource();
            if (info == null) {
                return;
            }

            if (className.equals("com.tencent.mm.ui.LauncherUI")){
                Log.d("RobberService", className);
            }

            if(className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")){
                Log.d("RobberService", className + " --- open");
                AccessibilityNodeInfo open = info.getChild(3);
                if(open != null && open.isClickable()){
                    open.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }

            if(className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")){
                Log.d("RobberService", className + " ---- back");
                AccessibilityNodeInfo back = info.getChild(0).getChild(1).getChild(0);
                if(back != null && back.isClickable()){
                    back.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }

        }
    }

    private int getNodes(AccessibilityNodeInfo info) {
        for (int i = 0; i < info.getChildCount(); i++) {
            AccessibilityNodeInfo sub = info.getChild(i);
            if (sub != null && sub.getChildCount() == 0){
                list.add(sub);
            }
            getNodes(sub);
        }
        return list.size();
    }

    private int getIndex(String str) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getText() != null) {
                String text = list.get(i).getText().toString();
                if (text.contains(str)) {
                    index = i;
                }
            }
        }
        return index;
    }

    private void getInfo(AccessibilityNodeInfo info, String s) {
        for (int i = 0; i < info.getChildCount(); i++) {
            AccessibilityNodeInfo sub = info.getChild(i);
            if (sub != null) {
                Log.d("RobberService", s + i + "----id: " + sub.toString());
                Log.d("RobberService", s + i + "----name: " + sub.getClassName());
                Log.d("RobberService", s + i + "----count: " + sub.getChildCount());
                Log.d("RobberService", s + i + "----text: " + sub.getText());
                Log.d("RobberService", s + i + "----click: " + sub.isClickable());
            }

            if (sub.getChildCount() > 0) {
                String str = s + "   ";
                getInfo(sub, str);
            }
        }
    }

    /**
     * getMoney()方法模拟点击“拆红包”
     */
    private void getMoney(){
        List<AccessibilityNodeInfo> list = new ArrayList();
        int iIndex = -1;
        //获取“拆红包”对话框中的所有NodeInfo，目前有5个
        for (int i = 0; i < mAccessibilityNodeInfo.getChildCount(); i++){
            list.add(mAccessibilityNodeInfo.getChild(i));
        }
        //判断是否有“给你发了一个红包”的view，并获取它在list中的位置。该view是目前在拆红包对话框中固定的一个view
        List<AccessibilityNodeInfo> tmpList = mAccessibilityNodeInfo.findAccessibilityNodeInfosByText("给你发了一个红包");
        if(tmpList.size() == 1){
            AccessibilityNodeInfo tmpInfo = tmpList.get(0);
            if(tmpInfo != null && ("给你发了一个红包".equals(tmpInfo.getText().toString()) || ("发了一个红包，金额随机".equals(tmpInfo.getText().toString())))){
                iIndex = list.indexOf(tmpInfo);
            }
        }
        //“给你发了一个红包”view之后的第一个可点击的View就是拆红包按钮，以前显示“拆红包”三个字，现在给成“開”的图片了
        if(iIndex != -1){
            for (int i = iIndex + 1; i < mAccessibilityNodeInfo.getChildCount(); i++){
                    if (list.get(i).isClickable()){
                        list.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
            }
        }
    }

    /**
     * backToWechat()方法抢完红包之后返回微信界面，等待下一个红包的到来
     */
    private void backToWechat(){
        AccessibilityNodeInfo info = mAccessibilityNodeInfo.getChild(0).getChild(1);
        if(info != null){
            return;
        }
        List<AccessibilityNodeInfo> list = new ArrayList();
        int iIndex = -1;
        for(int i = 0; i < info.getChildCount(); i++){
            list.add(info.getChild(i));
        }
        //判断是否有“红包详情”View，并且通过它获取它的父View
        List<AccessibilityNodeInfo> tmpList = mAccessibilityNodeInfo.findAccessibilityNodeInfosByText("红包详情");
        if(tmpList.size() == 1){
            AccessibilityNodeInfo tmpInfo = tmpList.get(0);
            if(tmpInfo != null && "红包详情".equals(tmpInfo.getText().toString())){
                iIndex = list.indexOf(tmpInfo);
            }
        }
        //返回微信聊天界面
        if (iIndex != -1){
            for (int i = iIndex - 1; i >= 0; i--){
                if (list.get(i).isClickable()){
                    list.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
