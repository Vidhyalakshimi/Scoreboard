import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

public class ZKCreate {
   // create static instance for zookeeper class.
   private static ZooKeeper zk;

   // create static instance for ZooKeeperConnection class.
   private static ZooKeeperConnection conn;

   // Method to create znode in zookeeper ensemble
   public static void create(String path, byte[] data) throws 
      KeeperException,InterruptedException {
      zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
      CreateMode.PERSISTENT);
   }

   // Run command create <host> <path>
   public static void main(String[] args) {
      String host = args[0];
      String path = args[1];
      byte[] data = "My first zookeeper app".getBytes(); 
		
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect(host);
         create(path, data); 
         conn.close();
      } catch (Exception e) {
         System.out.println(e.getMessage()); 
      }
   }
}