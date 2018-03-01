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
public class MasterSync implements Watcher {

    private ZooKeeper zk;
    private String hostPort;

    public MasterSync(String hostPort){
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

    AsyncCallback.StringCallback masterCreateCallback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch(KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    checkMaster();
                    return;
                case OK:
                    isLeader = true;
                    break;
                default:
                    isLeader = false;
            }
            System.out.println("I am " + (isLeader ? "" : "not ") + "the leader");
        }
    };

    AsyncCallback.DataCallback masterCheckCallback = new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    checkMaster();
                case NONODE:
                    runForMaster();
                    return;
            }
        }
    };

    void checkMaster(){
        zk.getData("/master",false,masterCheckCallback,null);
    }


    void runForMaster(){
        zk.create("/master",serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL,masterCreateCallback,null);
    }

    void stopZK() throws InterruptedException {
        zk.close();
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    public static void main(String[] args) throws Exception {
        MasterSync master = new MasterSync("172.16.10.102:2181");

        master.startZK();

        master.runForMaster();


        master.stopZK();
    }

}
