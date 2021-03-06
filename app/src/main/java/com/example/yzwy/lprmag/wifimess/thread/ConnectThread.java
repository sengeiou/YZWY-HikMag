package com.example.yzwy.lprmag.wifimess.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.example.yzwy.lprmag.dialog.MessageDialog;
import com.example.yzwy.lprmag.myConstant.WifiMsgConstant;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 發送和接收線程
 * 连接线程
 * Created by syhuang on 2016/9/7.
 */
public class ConnectThread extends Thread {

    private final Socket socket;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;

//    private OnConnecListener onConnecListener;

    public ConnectThread(Socket socket, Handler handler) {
        setName("ConnectThread");
        Log.i("ConnectThread", "ConnectThread");
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {
/*        if(activeConnect){
//            socket.c
        }*/
        if (socket == null) {
            return;
        }
        handler.sendEmptyMessage(WifiMsgConstant.DEVICE_CONNECTED);
        try {
            //获取数据流
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytes;
            while (!isInterrupted()) {

                //中断处理逻辑
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("DiaLogPersetActivity" + "The thread is interrupted!");
                    break;
                }
                try {
                    //读取数据
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        final byte[] data = new byte[bytes];
                        System.arraycopy(buffer, 0, data, 0, bytes);

                        Message message = Message.obtain();
                        message.what = WifiMsgConstant.GET_MSG;
                        Bundle bundle = new Bundle();
                        bundle.putString("MSG", new String(data));
                        message.setData(bundle);
                        handler.sendMessage(message);

                        Log.i("ConnectThread", "读取到数据:" + new String(data));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    break;
                    //中断状态在抛出异常前，被清除掉，因此在此处重置中断状态
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //中断状态在抛出异常前，被清除掉，因此在此处重置中断状态
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 发送数据
     */
    public ConnectThread sendData(final String msg) {
        Log.i("ConnectThread", "发送数据:" + (outputStream == null));
        if (outputStream != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(msg.getBytes());
                        outputStream.flush();
                        Log.i("ConnectThread", "发送消息：" + msg);
                        Message message = Message.obtain();
                        message.what = WifiMsgConstant.SEND_MSG_SUCCSEE;
                        Bundle bundle = new Bundle();
                        bundle.putString("MSG", new String(msg));
                        message.setData(bundle);
                        handler.sendMessage(message);

                        //onConnecListener.onSuccess(new String(msg));

                    } catch (IOException e) {
                        e.printStackTrace();
                        Message message = Message.obtain();
                        message.what = WifiMsgConstant.SEND_MSG_ERROR;
                        Bundle bundle = new Bundle();
                        bundle.putString("MSG", new String(msg));
                        message.setData(bundle);
                        handler.sendMessage(message);

                        //onConnecListener.onFailure(new String(msg));

                    }
                }
            }).start();
        }

        return this;

    }


    /**
     * @param file
     */
    public void sendData(File file) {
        Log.i("ConnectThread", "发送数据:" + (outputStream == null));
        if (outputStream != null) {
            try {
                FileInputStream fis = null;
                DataOutputStream dos = null;
                if (file.exists()) {
                    fis = new FileInputStream(file);
                    dos = new DataOutputStream(socket.getOutputStream());

                    // 文件名和长度
                    dos.writeUTF(file.getName());
                    dos.flush();
                    dos.writeLong(file.length());
                    dos.flush();

                    // 开始传输文件
                    Log.i("ConnectThread", "======== 开始传输文件 ========" + file.getName());
                    Log.i("ConnectThread", "fileName:" + file.getName() + "");
                    Log.i("ConnectThread", "fileLength:" + file.length() + "");
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    long progress = 0;
                    while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                        dos.write(bytes, 0, length);
                        dos.flush();
                        progress += length;
                        Log.i("ConnectThread", "| " + (100 * progress / file.length()) + "% |");
                    }
                    Log.i("ConnectThread", "======== 文件传输成功 ========");
                    Message message = Message.obtain();
                    message.what = WifiMsgConstant.SEND_MSG_SUCCSEE;
                    Bundle bundle = new Bundle();
                    bundle.putString("MSG", new String("文件传输成功"));
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = WifiMsgConstant.SEND_MSG_ERROR;
                Bundle bundle = new Bundle();
                bundle.putString("MSG", new String("发送文件失败"));
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }
    }


    public void close() {
        //this.close();
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public ConnectThread setOnConnecListener(OnConnecListener onConnecListener) {
//        this.onConnecListener = onConnecListener;
//        return this;
//    }
//
//    public interface OnConnecListener {
//
//        /**
//         * 点击确定时回调
//         */
//        void onSuccess(String str, String command);
//
//        /**
//         * 点击确定时回调
//         */
//        void onSuccess(String str);
//
//        /**
//         * 点击取消时回调
//         */
//        void onFailure(String str, String command);
//
//        /**
//         * 点击取消时回调
//         */
//        void onFailure(String str);
//    }
}
