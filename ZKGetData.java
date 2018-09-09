import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
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
   private static ZooKeeperConnection conn;
   private static String path;
   private static String host;
   private HashMap<String, Integer> data = new HashMap<String, Integer>();
   
   ZKGetData(String path, String host) {
	   this.path = path;
	   this.host= host;	   
   }
   
   private static void printHashMap(HashMap<String, Integer> hm ) {
	   System.out.println("Data");
	   for(Map.Entry<String, Integer> data : hm.entrySet()) {
		   System.out.println(data.getKey() + ": " + data.getValue());
	   }
	   System.out.println(hm.size());
		  
   }
   
   public static Stat znode_exists(String path) throws 
      KeeperException,InterruptedException {
      return zk.exists(path,true);
   }
//   public static void main(String[] args) throws InterruptedException, KeeperException {

   public HashMap<String, Integer> getData() throws InterruptedException, KeeperException {
      final CountDownLatch connectedSignal = new CountDownLatch(1);
		
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect(host);
         System.out.println(path);
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
                        	data = (HashMap<String, Integer>)obj.readObject();	
                        	System.out.println("Inside Watcher");
                        	printHashMap(data);
                        	System.out.println("Inside Watcher end");
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
            	this.data = (HashMap<String, Integer>) obj.readObject();	
                printHashMap(data);

            	System.out.println("Outside Watcher");
            	
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
      printHashMap(data);
      return data;
   }
}