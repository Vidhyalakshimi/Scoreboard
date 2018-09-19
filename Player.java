import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class Player {
   private ZooKeeper zk;
   private ZooKeeperConnection conn;
   private ZKGetData getdata;
   private ZKSetData setdata;
   private String path;
   private String host;
   private String playerName;
   Player(String path, String host) throws IOException, InterruptedException {
	   this.host = host;
	   setupConnection();
	   this.getdata = new ZKGetData(this.zk);
	   this.setdata = new ZKSetData(this.zk);
	   this.path = path;
   	}
   
   private void setupConnection() throws IOException, InterruptedException {
	     this.conn = new ZooKeeperConnection();
	     this.zk = conn.connect(this.host);
   }

   private void closeConnection() {
	   try {
		this.conn.close();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
   }

   private void printLinkedHashMap(LinkedHashMap<String, Integer> hm ) {
	   // Might not preserve order
	   List<String> keys = new ArrayList<String>(hm.keySet());   
       Collections.reverse(keys);
       for(String strKey : keys){
           System.out.println(strKey + ": "  + hm.get(strKey));
       }
   }
   
   private boolean join(String playerName) throws InterruptedException, KeeperException, IOException {
	   List<LinkedHashMap<String, Integer>> data = getdata.getData(this.path);	   
	   List<LinkedHashMap<String, Integer>> updatedData = new ArrayList<LinkedHashMap<String, Integer>>();
	   LinkedHashMap<String, Integer> recent;
	   LinkedHashMap<String, Integer> highest;
	   LinkedHashMap<String, Integer> online;
	   if(data.size() == 0) {
		   recent= new LinkedHashMap<String, Integer>();
		   highest= new LinkedHashMap<String, Integer>();
		   online= new LinkedHashMap<String, Integer>();
	   } else {
		    recent = data.get(0);
		   	highest = data.get(1);  
		   	online = data.get(2);
	   }
	   this.playerName = playerName;
	   if(online.containsKey(playerName)){
		   return false;
	   }
	   online.put(playerName, 1);
	   
	   updatedData.add(recent);
	   updatedData.add(highest);
	   updatedData.add(online);
	   System.out.println("Player " + playerName + " has joined!");
	   setdata.setData(this.path, updatedData);
	   return true;
	}
   
   private void submitScore(int score) throws InterruptedException, KeeperException, IOException {
	   
	   List<LinkedHashMap<String, Integer>> data = getdata.getData(this.path);	   
	   List<LinkedHashMap<String, Integer>> updatedData = new ArrayList<LinkedHashMap<String, Integer>>();
	   LinkedHashMap<String, Integer> recent;
	   LinkedHashMap<String, Integer> highest;
	   LinkedHashMap<String, Integer> online;
	   if(data.size() == 0) {
		   recent= new LinkedHashMap<String, Integer>();
		   highest= new LinkedHashMap<String, Integer>();
		   online= new LinkedHashMap<String, Integer>();
	   } else {
			recent = data.get(0);
		   	highest = data.get(1);  
		   	online = data.get(2);
	   }
	   			
	   String name =  this.playerName +"_"+ Long.toString(System.currentTimeMillis());
	   recent.put(name, score);
	   highest.put(name, score);
	   online.put(this.playerName, 1);
	   
	   updatedData.add(recent);
	   updatedData.add(highest);
	   updatedData.add(online);

	   setdata.setData(this.path, updatedData);
	}
   
   private void leave() throws InterruptedException, KeeperException, IOException {
	   List<LinkedHashMap<String, Integer>> data = getdata.getData(this.path);	   
	   List<LinkedHashMap<String, Integer>> updatedData = new ArrayList<LinkedHashMap<String, Integer>>();
	   LinkedHashMap<String, Integer> online;
	   online = data.get(2);
	   online.remove(this.playerName);

	   updatedData.add(data.get(0));
	   updatedData.add(data.get(1));
	   updatedData.add(online);
	   System.out.println("Player " + this.playerName + " has left!");
	   setdata.setData(this.path, updatedData);

   }
   
   private void interactiveMode(Player play)
			throws IOException, InterruptedException, KeeperException {
		Scanner s = new Scanner(System.in);
      	System.out.println("Enter Score or 'quit' to exit game: ");
      	String choice = s.nextLine();
	   
		while(!choice.equalsIgnoreCase("quit")){
		 //Submit Score
		   int score = Integer.parseInt(choice);
		   play.submitScore(score);
		   System.out.println("Enter Score or 'quit' to exit game: ");
		   choice  = s.nextLine();
	   }
	   play.leave();   
	}
   
   private void  automatedMode(Player play, int count, int uDelay, int uScore) throws InterruptedException, KeeperException, IOException {
	   Random r = new  Random();
	   int score = 0;
	   for(int i=1; i<= count; ++i) {
		   score = Math.abs((int)(r.nextGaussian()*90+uScore));
		   System.out.println(score);
		   play.submitScore(score);
		   Thread.sleep(3*score);
	   }
	   play.leave();   
   }

   // Run command player <host> <PlayerName> <count> <uDelay> <uScore>
   public static void main(String[] args) throws InterruptedException,KeeperException, IOException {
	   String path = "/ScoreBoard";
	   String host = args[0];
	   String playerName = args[1];
	   Player play = new Player(path, host);
	   //Player Joins
	   boolean status = play.join(playerName);
	   if(status) {
		   if(args.length == 5){
			   int count = Integer.parseInt(args[2]);
			   int uDelay = Integer.parseInt(args[3]);
			   int uScore = Integer.parseInt(args[4]);
			   play.automatedMode(play, count, uDelay, uScore);
	   
		   } else {
			   play.interactiveMode(play);   
		   }   
	   } else {
		   System.out.println("Player name already online");
	   }
	   play.closeConnection();
      }
}