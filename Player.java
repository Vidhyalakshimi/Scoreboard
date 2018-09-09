import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Player {
   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;
   private static ZKGetData getdata = new ZKGetData("/ScoreBoard", "localhost");
   private static ZKSetData setdata = new ZKSetData("/ScoreBoard", "localhost");
   
   private static void printHashMap(HashMap<String, Integer> hm ) {
	   for(Map.Entry<String, Integer> data : hm.entrySet()) {
		   System.out.println(data.getKey() + ": " + data.getValue());
	   }
   }
   public static void main(String[] args) throws InterruptedException,KeeperException {
      String path= "/ScoreBoard";
      
      try {
        HashMap<String, Integer> hm = getdata.getData();
        System.out.println("First");
        printHashMap(hm);
        hm.put("Vidhya0",200);
        System.out.println("Second");
        printHashMap(hm);
        setdata.setData(hm);
      } catch(Exception e) {
         System.out.println(e.getMessage());
      }
   }
}