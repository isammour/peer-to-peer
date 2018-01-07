/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author isammour
 */
public class RServer {
    
    public static void main(String[] args) {
        new Thread(new Server()).start();
    }
    
     
}
class Server implements Runnable
    {
        public LinkedHashSet<String> usernames = new LinkedHashSet<String>();
        public List<ClientStruct> users = new ArrayList<ClientStruct>();
        ServerSocket server;
        public Server()
        {
            try
            {
                server = new ServerSocket(5060);
            }
            catch(Exception ex)
            {
            }
        }

        public void run() 
        {
            Socket client;
            DataInputStream in;
            DataOutputStream out;
            ObjectOutputStream outObject;
            // 0 for initial connection and 1 for listing clients
            int code;
            int port;
            String username;
            boolean isRegistered = false;
            int state;
            ClientStruct cPing;
            
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
            @Override
                public void run() {
                    for(ClientStruct c : users)
                    {
                        if(!c.Ping)
                            users.remove(c);
                        else
                            c.Ping = false;
                    }
                }
            }, 2*60*1000);
            
            while(true)
            {
                try
                {
                    client = server.accept();
                    String ip = client.getInetAddress().getHostAddress();
                    System.out.println(ip);
                    in = new DataInputStream(client.getInputStream());
                    out = new DataOutputStream(client.getOutputStream());
                    outObject = new ObjectOutputStream(client.getOutputStream());
                    code = in.readInt();
                    System.out.println(code);
                    switch (code) {
                        case 0:
                            username = in.readUTF();
                            for(ClientStruct c : users)
                            {
                                if(c.Name.equals(username) && c.Ip.equals(ip))
                                {
                                    isRegistered = true;
                                    System.out.println("Duplicate!!!");
                                    break;
                                }
                            }   
                            if(!usernames.add(username) && !isRegistered)
                            {
                                out.writeInt(0);
                            }
                            else      
                            {
                                out.writeInt(1);
                                port = in.readInt();
                                System.out.println(port);
                                usernames.add(username);
                                users.add(new ClientStruct(username,ip,port));
                            }   break;
                        case 1:
                            outObject.writeObject(users);
                            break;
                        case 2:
                            break;
                        default:
                            username = in.readUTF();
                            state = in.readInt();
                            for(ClientStruct c : users)
                            {
                                if(c.Name.equals(username))
                                {
                                    c.Ping = true;
                                    if(state == 0)
                                        c.State = "Available";
                                    else
                                        c.State = "Busy";
                                }
                            }   break;
                    }
                    isRegistered = false;
                }
                catch(Exception ex)
                {
                }
            }
        }
    }
    
    class ClientStruct implements java.io.Serializable 
    {
        public String Name;
        public String Ip;
        public int Port;
        public String State;
        public boolean Ping; 
        
        public ClientStruct(String name,String ip,int port)
        {
            this.Name = name;
            this.Ip = ip;
            this.Port = port;
            this.State = "Available";
            this.Ping = true;
        }
    }
   
