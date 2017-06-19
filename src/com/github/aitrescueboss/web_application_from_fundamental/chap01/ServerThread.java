package com.github.aitrescueboss.web_application_from_fundamental.chap01;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerThread implements Runnable {

    private static final String DOCUMENT_ROOT = "/Users/boss";
    private Socket mSocket;

    public ServerThread(Socket aSocket) {
        mSocket = aSocket;
    }

    private static String readLine(InputStream aInput) throws Exception {
        int tReadChar;
        String tConcated = "";

        while((tReadChar = aInput.read()) != -1) {
            if(tReadChar == '\r') {
                // do nothing.
            } else if(tReadChar == '\n') {
                break;
            } else {
                tConcated += (char)tReadChar;
            }
        }

        if(tReadChar == -1) {
            return "";
        }
        return tConcated;
    }

    private static void writeLine(OutputStream aOutput, String aString) throws Exception {
        for(char tChar : aString.toCharArray()) {
            aOutput.write(tChar);
        }
        // CR+LFを書き込む
        aOutput.write((int)'\r');
        aOutput.write((int)'\n');
    }

    private static String getDateStringUTC() {
        Calendar tCal = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ));
        DateFormat tDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        tDateFormat.setTimeZone(tCal.getTimeZone());
        return tDateFormat.format(tCal.getTime()) + " GMT";
    }

    private static final Map<String, String> mExtensionToContentType =
            new HashMap<String, String>(){
                {
                    put("html", "text/html");
                    put("htm", "text/html");
                    put("txt", "text/plain");
                    put("css", "text/css");
                    put("png", "image/png");
                    put("jpg", "image/jpeg");
                    put("jpeg", "image/jpeg");
                    put("gif", "image/gif");
                }
            };

    private static String getContentType(String aExtension) {
        String tRet = mExtensionToContentType.get(aExtension.toLowerCase());
        if(tRet == null) {
            return "application/octet-stream";
        }
        return tRet;
    }

    @Override
    public void run() {
        OutputStream tOutputToClient;
        try{
            InputStream tInputFromClient = mSocket.getInputStream();

            String tLine;
            String tPath = null;
            String tExt = null;

            while((tLine = readLine(tInputFromClient)) != null) {
                if(tLine.equals( "" )) {
                    break;
                }
                if(tLine.startsWith( "GET" )) {
                    tPath = tLine.split(" ")[1];
                    String[] tmp = tPath.split("\\.");
                    tExt = tmp[tmp.length - 1];
                }
            }

            tOutputToClient = mSocket.getOutputStream();

            writeLine(tOutputToClient, "HTTP/1.1 200 OK");
            writeLine(tOutputToClient, "Date: " + getDateStringUTC());
            writeLine(tOutputToClient, "Server: Modoki/0.1");
            writeLine(tOutputToClient, "Connection: close");
            writeLine(tOutputToClient, "Content-Type: " + getContentType(tExt));
            writeLine(tOutputToClient, "");

            try(FileInputStream tInputFromDocument = new FileInputStream( DOCUMENT_ROOT + tPath )) {
                int tChar;
                while((tChar = tInputFromDocument.read()) != -1) {
                    tOutputToClient.write( tChar );
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mSocket.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
