package com.example.microstart.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author lzj
 * @date 2019/4/28
 */
public class ZookeeperDemo {

    private static final String CONNECTION_STRING = "47.102.147.70:2181";
//    连接zk集群
//    private static final String CONNECTION_STRING = "47.102.147.70:2181,47.102.147.70:2182,47.102.147.70:2183";

    private static final int SESSION_TIMEOUT = 5000;

    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        //连接zookeeper
        ZooKeeper zk = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                latch.countDown();
            }
        });

        await();

//        List<String> children = zk.getChildren("/", null);
//        children.forEach(System.out::println);

        zk.getChildren("/", null, (int resultCode, String path, Object ctx, List<String> list) -> {
            list.forEach(System.out::println);
        }, null);

        String name = zk.create("/foo", "hello_idea".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(name);


        Thread.sleep(Long.MAX_VALUE);
    }

    private static void await() {
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
    }
}
