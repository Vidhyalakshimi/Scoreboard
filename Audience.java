	
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;


public class Audience {

   private ZooKeeper zk;
   private ZooKeeperConnection conn;
   private ZKGetData getdata;
   private String path;
   private String host;
   
   Audience(String path, String host) throws IOException, InterruptedException {
	   setupConnection();
	   this.getdata = new ZKGetData(this.zk);
	   this.path = path;
	   this.host = host;
   	}
   
   private void setupConnection() throws IOException, InterruptedException {
	     this.conn = new ZooKeeperConnection();
	     this.zk = conn.connect("localhost");
   }


	private static void printLinkedHashMap(LinkedHashMap<String, Integer> hm ) {
		   // Might not preserve order
		   List<String> keys = new ArrayList<String>(hm.keySet());   
	       Collections.reverse(keys);
	       for(String strKey : keys){
	           System.out.println(strKey + ": "  + hm.get(strKey));
	       }
	   }
	
	private void watch() throws InterruptedException, KeeperException {
		LinkedHashMap<String, Integer> data = this.getdata.getData(this.path);
		System.out.println("Most Recent Scores:");
		printLinkedHashMap(data);
	}


   public static void main(String[] args) throws InterruptedException, KeeperException, IOException {
	   String path = "/ScoreBoard";
	   String host = "localhost";
	   
	   Audience aud = new Audience(path, host);
	   aud.watch();
   }
}
