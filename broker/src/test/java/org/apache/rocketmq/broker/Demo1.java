package org.apache.rocketmq.broker;

import org.apache.rocketmq.common.constant.PermName;

/**
 * @author zhaiyanan
 * @date 2019/1/14 15:21
 */
public class Demo1 {

    public static void main(String[] args) {

        int perm = PermName.PERM_READ | PermName.PERM_WRITE;
        boolean flag = PermName.isInherited(perm);
        System.out.println(flag);
    }
}
