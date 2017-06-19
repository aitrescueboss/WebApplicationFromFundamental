package com.github.aitrescueboss.web_application_from_fundamental.chap01;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Modoki01 {

    private static final String DOCUMENT_ROOT = "/Users/boss";

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
        Calendar tCal = Calendar.getInstance( TimeZone.getTimeZone("UTC"));
        DateFormat tDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        tDateFormat.setTimeZone(tCal.getTimeZone());
        return tDateFormat.format(tCal.getTime()) + " GMT";
    }

    public static void main(String[] args) throws Exception {
        try(ServerSocket tServer = new ServerSocket(8001)) {

            //========================================================
            // クライアントからの接続を待ち受ける.
            //========================================================
            Socket tSocketToClient = tServer.accept();

            //========================================================
            //  クライアントからのリクエストを読む.
            //========================================================
            InputStream tInputFromClient = tSocketToClient.getInputStream();
            String tReadLine = "";
            String tPath = "";
            while((tReadLine = readLine(tInputFromClient)) != null) {
                if(tReadLine.equals("")) {
                    break;
                }
                if(tReadLine.startsWith("GET")) {
                    tPath = tReadLine.split(" ")[1];
                }

                // リクエストヘッダを標準出力に吐く.
                System.out.println(tReadLine);
            }

            //========================================================
            // レスポンスヘッダを返す.
            //========================================================
            OutputStream tOutputToClient = tSocketToClient.getOutputStream();
            writeLine(tOutputToClient, "HTTP/1.1 200 OK");
            writeLine(tOutputToClient, "Date: " + getDateStringUTC());
            writeLine(tOutputToClient, "Server: Modoki/0.1");
            writeLine(tOutputToClient, "Connection: close");
            writeLine(tOutputToClient, "Content-type: text/html");

            writeLine(System.out, "------------------------");
            writeLine(System.out, "HTTP/1.1 200 OK");
            writeLine(System.out, "Date: " + getDateStringUTC());
            writeLine(System.out, "Server: Modoki/0.1");
            writeLine(System.out, "Connection: close");
            writeLine(System.out, "Content-type: text/html");

            //========================================================
            // レスポンスボディを返す.
            //========================================================
            try(FileInputStream tInputFromFile = new FileInputStream(DOCUMENT_ROOT + tPath)) {
                int tReadCharFromFile;
                while((tReadCharFromFile = tInputFromFile.read()) != -1) {
                    tOutputToClient.write(tReadCharFromFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //========================================================
            // Finalize.
            //========================================================
            tSocketToClient.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
