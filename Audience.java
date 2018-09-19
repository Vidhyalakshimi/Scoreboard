	
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;


public class Audience {

   private ZooKeeper zk;
   private ZooKeeperConnection conn;
   private ZKGetData getdata;
   private String path;
   private String host;
   private int list_size;
   public static List<LinkedHashMap<String, Integer>> data;
   
   Audience(String path, String host, int n) throws IOException, InterruptedException {
	   this.host = host;
	   setupConnection();
	   this.getdata = new ZKGetData(this.zk);
	   this.path = path;
	   this.list_size = n;
	   this.data = new ArrayList<LinkedHashMap<String, Integer>>();
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

	private static void printReverseLinkedHashMap(LinkedHashMap<String, Integer> hm ) {
		   // Might not preserve order
		   List<String> keys = new ArrayList<String>(hm.keySet());   
	       Collections.reverse(keys);
	       for(String strKey : keys){
	           System.out.println(strKey + ": "  + hm.get(strKey));
	       }
	   }
	
	private static void printLinkedHashMap(LinkedHashMap<String, Integer> hm, LinkedHashMap<String, Integer> online ) {
		   // Might not preserve order
		   List<String> keys = new ArrayList<String>(hm.keySet());   
	       for(String strKey : keys){
	    	   String realName = strKey.split("_")[0];
	    	   System.out.print(realName + ": "  + hm.get(strKey));
	           if(online.containsKey(realName))
	        	   System.out.print(" **");
	           System.out.println();	       }
	   }
	
	private void watch() throws InterruptedException, KeeperException {
//		List<LinkedHashMap<String, Integer>> data = this.getdata.getData(this.path);
	   LinkedHashMap<String, Integer> recent= this.data.get(0);
	   LinkedHashMap<String, Integer> highest= this.data.get(1);
	   LinkedHashMap<String, Integer> online= this.data.get(2);
	   
	   System.out.flush();
	   //Update most recent scores
	   System.out.println("Most Recent Scores:");
	   List<String> keys = new ArrayList<String>(recent.keySet());   
       Collections.reverse(keys);
       int n = Math.min(this.list_size, keys.size());
       for(String strKey : keys.subList(0, n)){
    	   String realName = strKey.split("_")[0];
    	   System.out.print(realName + ": "  + recent.get(strKey));
           if(online.containsKey(realName))
        	   System.out.print(" **");
           System.out.println();
       }
       
	   //Update most highest scores
	   highest = highest.entrySet().stream()
       .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
       .limit(this.list_size)
       .collect(Collectors.toMap(
          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));		   
		System.out.println("Most Highest Scores:");
		printLinkedHashMap(highest, online);		
	}

	 public static Stat znode_exists(String path, ZooKeeper zk) throws 
     KeeperException,InterruptedException {
     return zk.exists(path,true);
	 }
	 
	   // Run command watcher <host> <list size>
   public static void main(String[] args) throws InterruptedException, KeeperException, IOException {
	   String path = "/ScoreBoard";
	   String host = args[0];
	   int n = Integer.parseInt(args[1]);
	   Audience aud = new Audience(path, host, n);
	   while(true){
	   final CountDownLatch connectedSignal = new CountDownLatch(1);
		
	      try {
	         Stat stat = znode_exists(path, aud.zk);
	         if(stat != null) {
	            byte[] b = aud.zk.getData(path, new Watcher() {
					
	               public void process(WatchedEvent we) {
						
	                  if (we.getType() == Event.EventType.None) {
	                     switch(we.getState()) {
	                        case Expired:
	                        connectedSignal.countDown();
	                        break;
	                     }		
	                  } else {							
	                     try {
	                        byte[] bn = aud.zk.getData(path, false, null);
	                        ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(bn));
	                        try{
	                        	aud.data = (ArrayList<LinkedHashMap<String, Integer>>)obj.readObject();	
	                        }                        
	                        catch(Exception e){
	                        	e.printStackTrace();
	                        }
	                        obj.close();
	                        connectedSignal.countDown();
								
	                     } catch(Exception ex) {
	                        System.out.println(ex.getMessage());
	                     }
	                  }
	               }
	            }, null);
	            
	        	ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(b));
	        	try{
	            	aud.data = (ArrayList<LinkedHashMap<String, Integer>>) obj.readObject();	
	         	    aud.watch();
	        	} 
	            catch(Exception e){
	            	e.printStackTrace();
	            }
	            obj.close();
	            connectedSignal.await();
	         } else {
	            System.out.println("Node does not exists");
	         }
	      } catch(Exception e) {
	        System.out.println(e.getMessage());
	      }
	      aud.closeConnection();
	   }
   }
}
