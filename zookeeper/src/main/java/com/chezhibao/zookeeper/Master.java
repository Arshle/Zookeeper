/*
 * FileName: Master.java
 * Author:   Arshle
 * Date:     2018年02月26日
 * Description:
 */
package com.chezhibao.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

/**
 * 〈〉<br>
 * 〈〉
 *
 * @author Arshle
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本]（可选）
 */
public class Master implements Watcher {

    private ZooKeeper zk;
    private String hostPort;

    public Master(String hostPort){
        this.hostPort = hostPort;
    }
    /**
     * 启动zookeeper连接
     */
    public void startZK(){
        try {
            zk = new ZooKeeper(hostPort,15000,this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String serverId = Integer.toHexString(new Random().nextInt());
    boolean isLeader = false;

    boolean checkMaster() throws KeeperException, InterruptedException {
        while (true){
            try {
                Stat stat = new Stat();
                byte[] data = zk.getData("/master", false, stat);
                isLeader = new String(data).equals(serverId);
                return true;
            } catch (KeeperException.NoNodeException e) {
                e.printStackTrace();
                return false;
            } catch (KeeperException.ConnectionLossException e) {
                e.printStackTrace();
            }
        }
    }

    void runForMaster() throws InterruptedException, KeeperException {
        while (true){
            try {
                zk.create("/master", serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                isLeader = true;
                break;
            } catch (KeeperException.NodeExistsException e) {
                e.printStackTrace();
                isLeader = false;
                break;
            } catch (KeeperException.ConnectionLossException e) {
                e.printStackTrace();
            }
            if(checkMaster()){
                break;
            }
        }
    }

    void stopZK() throws InterruptedException {
        zk.close();
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    public static void main(String[] args) throws Exception {
        Master master = new Master("172.16.10.102:2181");

        master.startZK();

        master.runForMaster();

        if(master.isLeader){
            System.out.println("I am the leader");
            Thread.sleep(60000);
        }else{
            System.out.println("someone else is the leader");
        }

        master.stopZK();
    }

}
