import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class ZKSetData {
   private static ZooKeeper zk;
   
   ZKSetData(ZooKeeper zk) {
	   this.zk = zk;
   }

   // Method to update the data in a znode. Similar to getData but without watcher.
   public void update(String path, byte[] data) throws
      KeeperException,InterruptedException {
      zk.setData(path, data, zk.exists(path,true).getVersion());
   }
   
   private static void printHashMap(LinkedHashMap<String, Integer> hm ) {
	   for(Map.Entry<String, Integer> data : hm.entrySet()) {
		   System.out.println(data.getKey() + ": " + data.getValue());
	   }
   }
   
   public void setData(String path, List<LinkedHashMap<String, Integer>> map) throws IOException {
	   	  ByteArrayOutputStream obj = new ByteArrayOutputStream();
	   	  ObjectOutputStream out = new ObjectOutputStream(obj);
	      out.writeObject(map);
	      byte[] data = obj.toByteArray();
	      // Update znode data to the specified path
	   	  
	      try {
			update(path, data);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		} 
	   }
}