/*
 * FileName: Master.java
 * Author:   Arshle
 * Date:     2018年02月26日
 * Description:
 */
package com.chezhibao.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

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

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    public static void main(String[] args) throws InterruptedException {
        Master master = new Master("172.16.10.102:2181");

        master.startZK();

        Thread.sleep(60000);
    }
}
