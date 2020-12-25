package com.javaxiu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @Description: JavaXiuRedis客户端
 *
 * @Author: java秀 javaxiu@javaxiu.com
 * @Date: 2020/12/25 10:40
 * @Version V1.0
 */
public class JavaXiuRedis extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // TODO: insert action logic here
        // 启动可视化界面
        RedisClientUI ui = new RedisClientUI();
    }

}
