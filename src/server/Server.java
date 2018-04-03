package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	
	public static void main(String[] args) throws Exception {
        System.out.println("The Fire Alarm Monitor server is running.");
        ServerSocket Alarmlistener = new ServerSocket(3001);
        try {
            while (true) {
                new Handler(Alarmlistener.accept()).start();
            }
        } finally {
        	Alarmlistener.close();
        }
    }


    private static class Handler extends Thread {
    	private Socket socket;
        public Handler(Socket socket) {
            this.socket = socket;
        }

     
        
        
       
        
        public void run() {
            try {

               
            	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
              

                System.out.println("client Connecting");
                while (true) {
                  
                    
                    
                }
                
                
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                
             
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
}
