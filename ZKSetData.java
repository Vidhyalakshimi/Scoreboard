import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ZKSetData {
   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;
   private static String path;
   private static String host;
   
   ZKSetData(String path, String host) {
	   this.path = path;
	   this.host= host;	   
   }

   // Method to update the data in a znode. Similar to getData but without watcher.
   public void update(String path, byte[] data) throws
      KeeperException,InterruptedException {
      zk.setData(path, data, zk.exists(path,true).getVersion());
   }
   
   private static void printHashMap(HashMap<String, Integer> hm ) {
	   for(Map.Entry<String, Integer> data : hm.entrySet()) {
		   System.out.println(data.getKey() + ": " + data.getValue());
	   }
   }
   
   public void setData(HashMap<String, Integer> map) throws InterruptedException,KeeperException, IOException {
	   	  ByteArrayOutputStream obj = new ByteArrayOutputStream();
	   	  ObjectOutputStream out = new ObjectOutputStream(obj);
	      out.writeObject(map);
	      byte[] data = obj.toByteArray();
	   	  try {
	         conn = new ZooKeeperConnection();
	         zk = conn.connect(host);
	         update(path, data); // Update znode data to the specified path
	   	  } catch(Exception e) {
	         System.out.println(e.getMessage());
	      }
	   }
//   public static void main(String[] args) throws InterruptedException,KeeperException {
//      String path= "/MyFirstZnode";
//      byte[] data = "Success12".getBytes(); //Assign data which is to be updated.
//		
//      try {
//         conn = new ZooKeeperConnection();
//         zk = conn.connect("localhost");
//         update(path, data); // Update znode data to the specified path
//      } catch(Exception e) {
//         System.out.println(e.getMessage());
//      }
//   }
}