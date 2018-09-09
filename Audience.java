	
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

   private static ZKGetData zkdata = new ZKGetData("/ScoreBoard", "localhost");
   
   private static void printLinkedHashMap(LinkedHashMap<String, Integer> hm ) {
	   // Might not preserve order
	   List<String> keys = new ArrayList<String>(hm.keySet());   
       Collections.reverse(keys);
       for(String strKey : keys){
           System.out.println(strKey + ": "  + hm.get(strKey));
       }
   }

   public static void main(String[] args) throws InterruptedException, KeeperException {
	   LinkedHashMap<String, Integer> data = zkdata.getData();
	   System.out.println("Most Recent Scores:");
	   printLinkedHashMap(data);
   }
}
