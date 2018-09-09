import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

public class ZKGetData {

   private static ZooKeeper zk;
   private LinkedHashMap<String, Integer> data = new LinkedHashMap<String, Integer>();
   
   ZKGetData(ZooKeeper zk) {
	   this.zk = zk;
   }
   
   private static void printHashMap(LinkedHashMap<String, Integer> hm ) {
	   System.out.println("Data");
	   for(Map.Entry<String, Integer> data : hm.entrySet()) {
		   System.out.println(data.getKey() + ": " + data.getValue());
	   }	  
   }
   
   public static Stat znode_exists(String path) throws 
      KeeperException,InterruptedException {
      return zk.exists(path,true);
   }
//   public static void main(String[] args) throws InterruptedException, KeeperException {

   public LinkedHashMap<String, Integer> getData(String path) throws InterruptedException, KeeperException {
      final CountDownLatch connectedSignal = new CountDownLatch(1);
		
      try {
         Stat stat = znode_exists(path);
         System.out.println(stat);
         if(stat != null) {
            byte[] b = zk.getData(path, new Watcher() {
				
               public void process(WatchedEvent we) {
					
                  if (we.getType() == Event.EventType.None) {
                     switch(we.getState()) {
                        case Expired:
                        connectedSignal.countDown();
                        break;
                     }		
                  } else {							
                     try {
                        byte[] bn = zk.getData(path, false, null);
                        ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(bn));
                        try{
                        	data = (LinkedHashMap<String, Integer>)obj.readObject();	
//                        	System.out.println("Inside Watcher");
//                        	printHashMap(data);
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
            	this.data = (LinkedHashMap<String, Integer>) obj.readObject();	
                printHashMap(data);
        	} 
            catch(Exception e){
            	e.printStackTrace();
            }
            obj.close();
//            String data = new String(b, "UTF-8");
//            System.out.println(data);
//            connectedSignal.await();
         } else {
            System.out.println("Node does not exists");
         }
      } catch(Exception e) {
        System.out.println(e.getMessage());
      }
//      printHashMap(data);
      return data;
   }
}