/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import static Server.Client.conn;
import static Server.Client.in;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author isammour
 */
public class Client {
    static Socket conn;
    static DataInputStream in;
    static DataOutputStream out;
    static ObjectInputStream inObject;
    static Integer state = 0;
    static List<ClientStruct> clients;
    
    public static void main(String[] args)
    {
        System.out.print("Enter Username : ");
        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        try
        {
          String username = buffer.readLine();
          System.out.print("Enter Port : ");
          Integer port = Integer.parseInt(buffer.readLine());
          conn = new Socket(InetAddress.getLocalHost(), 5060);
          in = new DataInputStream(conn.getInputStream());
          out = new DataOutputStream(conn.getOutputStream());
          inObject = new ObjectInputStream(conn.getInputStream());
          out.writeInt(0);
          out.writeUTF(username);
          int result = in.readInt();
          System.out.println("Result is : "+result);
          if(result == 0)
          {
              System.out.println("Re-establish the connection with new username, it's taken !!!!!!!");
              return;
          }
          else
          {
              out.writeInt(port);
              System.out.println("Connection with the server has been established successfully.");
          }
          Timer timer = new Timer();
            timer.schedule(new TimerTask() {
            @Override
                public void run() {
                    try
                    {
                        conn = new Socket(InetAddress.getLocalHost(), 5060);
                        in = new DataInputStream(conn.getInputStream());
                        out = new DataOutputStream(conn.getOutputStream());
                        inObject = new ObjectInputStream(conn.getInputStream());
                        out.writeInt(2);
                        out.writeUTF(username);
                        out.writeInt(state);
                    }
                    catch(Exception ex){}
                }
            }, 1*60*1000);
            conn.close();
          new Thread(new ServerReq()).start();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
}

class ServerReq implements Runnable
{
    BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
    public void run() 
    {
        String option;
        while(true)
        {
            try
            {
                
               option = buffer.readLine();
               Client.conn = new Socket(InetAddress.getLocalHost(), 5060);
                Client.in = new DataInputStream(Client.conn.getInputStream());
                Client.out = new DataOutputStream(Client.conn.getOutputStream());
                Client.inObject = new ObjectInputStream(Client.conn.getInputStream());
               if("list".equals(option))
               {
                   System.out.println("list entered");
                   Client.out.writeInt(1);
                   Client.clients = (List<ClientStruct>)Client.inObject.readObject();
                   for(ClientStruct c:Client.clients)
                   {
                       System.out.println("username: "+c.Name+" ip: "+c.Ip+":"+c.Port+" state: "+c.State);
                   }
               }
               else
                   Client.out.writeInt(2);
            }
            catch(Exception ex){}
        }
    }
    
}

class Recieve implements Runnable
{
    public void run() 
    {
        
    }
}
class Send implements Runnable
{
    public void run() 
    {
        
    }
    
}

