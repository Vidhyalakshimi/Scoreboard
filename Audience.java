	
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;


public class Audience {

   private static ZKGetData zkdata = new ZKGetData("/ScoreBoard", "localhost");
   public static void main(String[] args) throws InterruptedException, KeeperException {
	   zkdata.getData();
   }
}
