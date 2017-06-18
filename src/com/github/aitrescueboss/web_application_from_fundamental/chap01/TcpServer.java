package com.github.aitrescueboss.web_application_from_fundamental.chap01;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {

    private static final int SERVER_SOCKET_PORT = 8001;
    private static final String DESTINATION_OF_OUTPUT_FROM_CLIENT = "./server_recv.txt";
    private static final String SOURCE_OF_INPUT_TO_CLIENT = "./server_send.txt";

    public static void main(String[] args) {

        try (ServerSocket mServer = new ServerSocket(SERVER_SOCKET_PORT);
            FileOutputStream mOutputStreamFromClient = new FileOutputStream(DESTINATION_OF_OUTPUT_FROM_CLIENT);
            FileInputStream mInputStreamToClient = new FileInputStream(SOURCE_OF_INPUT_TO_CLIENT);
        ) {
            //===========================================================================
            // Starting.
            //===========================================================================
            System.out.println("クライアントからの接続を待ちます...");
            Socket mSocket = mServer.accept(); //クライアントからの接続があると, mSocketにクライアントとの通信用ソケットが入る.
            System.out.println("クライアントからの接続が有りました．");

            //===========================================================================
            // 受信フェーズ:
            // クライアントから受信した何かをファイルに書き出す.
            //===========================================================================
            InputStream mInputStreamFromClient = mSocket.getInputStream();
            int ch = -1;
            while((ch = mInputStreamFromClient.read()) != 0) {
                // クライアントは, 終了の印として0を送ってくるので
                // 受信が終了するまで繰り返し.
                mOutputStreamFromClient.write(ch);
            }

            //===========================================================================
            // 送信フェーズ:
            // クライアントにファイルの中身を送信する.
            //===========================================================================
            OutputStream mOutputStreamToClient = mSocket.getOutputStream();
            while((ch = mInputStreamToClient.read()) != -1) {
                mOutputStreamToClient.write(ch);
            }

            //===========================================================================
            // Termination.
            //===========================================================================
            mSocket.close();
            System.out.println("通信終了.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
