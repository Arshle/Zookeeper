/*
 * FileName: CuratorTest.java
 * Author:   Arshle
 * Date:     2018年03月04日
 * Description:
 */
package com.chezhibao.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * 〈〉<br>
 * 〈〉
 *
 * @author Arshle
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本]（可选）
 */
public class CuratorTest {
    static CuratorFramework zkClient = CuratorFrameworkFactory.newClient("172.16.10.102",new RetryOneTime(1000));

    public static void main(String[] args) throws Exception {
        zkClient.start();
        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath("/arshle","zhangdanji".getBytes());
        TimeUnit.SECONDS.sleep(10);
        zkClient.close();
    }
}
