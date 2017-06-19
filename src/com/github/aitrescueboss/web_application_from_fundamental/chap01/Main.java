package com.github.aitrescueboss.web_application_from_fundamental.chap01;


import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        try(ServerSocket mServerSocket = new ServerSocket(8001)) {
            // 無限ループ.
            for(;;) {
                Socket tSocketToClient = mServerSocket.accept();
                ServerThread tServerThread = new ServerThread( tSocketToClient );
                Thread tServerThreadWrapper = new Thread(tServerThread);
                tServerThreadWrapper.start();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
