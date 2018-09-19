import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;

public class ZKDelete {
   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;

   // Method to check existence of znode and its status, if znode is available.
   public static void delete(String path) throws KeeperException,InterruptedException {
      zk.delete(path,zk.exists(path,true).getVersion());
   }
   
   // Run command delete <host> <path>
   public static void main(String[] args) throws InterruptedException,KeeperException {
      String host = args[0]; //Assign path to the znode
      String path = args[1];
      
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect(host);
         delete(path); //delete the node with the specified path
         conn.close();
      } catch(Exception e) {
         System.out.println(e.getMessage()); // catches error messages
      }
   }
}