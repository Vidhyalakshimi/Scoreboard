import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {
   private ZooKeeper zk;
   private ZooKeeperConnection conn;
   private ZKGetData getdata;
   private ZKSetData setdata;
   private int list_size;
   private String path;
   private String host;
   Player(String path, String host, int list_size) throws IOException, InterruptedException {
	   setupConnection();
	   this.getdata = new ZKGetData(this.zk);
	   this.setdata = new ZKSetData(this.zk);
	   this.list_size = list_size;
	   this.path = path;
	   this.host = host;
   	}
   
   private void setupConnection() throws IOException, InterruptedException {
	     this.conn = new ZooKeeperConnection();
	     this.zk = conn.connect("localhost");
   }

	private void printHashMap(HashMap<String, Integer> hm ) {
		   for(Map.Entry<String, Integer> data : hm.entrySet()) {
			   System.out.println(data.getKey() + ": " + data.getValue());
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
   
   private void submitScore(String player_name,
			int score) throws InterruptedException, KeeperException, IOException {
	   
	   LinkedHashMap<String, Integer> data = getdata.getData(this.path);
	   
	   if(data.size() < this.list_size){
		   if(data.containsKey(player_name)){
	   			  data.remove(player_name);
		   }
		  
	   } else {
		   if(data.containsKey(player_name)){
	   			data.remove(player_name);
		   } else {
			   String key = data.entrySet().iterator().next().getKey();   
			   data.remove(key);
		   }
	   }
	   data.put(player_name, score);
	   System.out.println("Final:");
	   printLinkedHashMap(data);
	   setdata.setData(this.path, data);
	}

   public static void main(String[] args) throws InterruptedException,KeeperException, IOException {
	   String path = "/ScoreBoard";
	   String host = "localhost";
	   int list_size = 3;
	   String player_name = "Vid412";
	   int score = 330;
	   
	   Player play = new Player(path, host, list_size);
	   //Submit Score
	   play.submitScore(player_name, score);
      }
}