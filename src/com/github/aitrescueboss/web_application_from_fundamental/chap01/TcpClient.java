package com.github.aitrescueboss.web_application_from_fundamental.chap01;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpClient {

    private static final String HOST = "localhost";
    private static final int PORT_TO_HOST = 8080;
    private static final String CONTENT_FOR_SERVER = "client_send.txt";
    private static final String DESTINATION_OF_RECEIVE = "client_recv.txt";

    public static void main(String[] args) {
        try(Socket tSocketToServer = new Socket(HOST, PORT_TO_HOST);
            FileInputStream tInputStreamForSendContent = new FileInputStream(CONTENT_FOR_SERVER);
            FileOutputStream tOutputStreamOfReceive = new FileOutputStream(DESTINATION_OF_RECEIVE);
        ) {

            //===========================================================================
            // 送信フェーズ:
            // クライアントからサーバにファイルの中身を送信する.
            //===========================================================================
            int ch;
            OutputStream tOutputToServer = tSocketToServer.getOutputStream();
            while((ch = tInputStreamForSendContent.read()) != -1) {
                tOutputToServer.write(ch);
            }
            // クライアントは, 送信終了の印として0を末尾に送る.
            tOutputToServer.write(0);

            //===========================================================================
            // 受信フェーズ:
            // サーバからの返信をファイルに出力
            //===========================================================================
            InputStream tInputFromServer = tSocketToServer.getInputStream();
            while((ch = tInputFromServer.read()) != -1) {
                tOutputStreamOfReceive.write(ch);
            }

            //===========================================================================
            // Termination.
            //===========================================================================
            tInputStreamForSendContent.close();
            tOutputStreamOfReceive.close();
            tOutputToServer.close();
            tSocketToServer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
