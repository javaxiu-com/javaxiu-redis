package com.javaxiu;

import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

/**
 * @Description: JavaXiuRedis客户端
 *
 * @Author: java秀 javaxiu@javaxiu.com
 * @Date: 2020/12/25 10:40
 * @Version V1.0
 */
public class RedisClientUI extends JFrame {

    private static final Logger log = Logger.getLogger(RedisClientUI.class);

    private static final String ERROR = "获取Redis链接出现异常,请检查Redis是否链接成功!";
    private static final String CONNECT_SUCCESS = "连接Redis成功！";
    private static final String SET_SUCCESS = "添加缓存成功！";
    private static final String DELETE_SUCCESS = "删除缓存成功！";
    private static final String PORT_NO_SURE = "输入的端口格式不正确!";
    private static final String PASSWORD_NO_SURE = "密码不正确!";
    private static final String INDEX_NO_SURE = "输入的索引格式不正确!";
    private static final String TIME_OUT_NO_SURE = "输入的超时时间格式不正确!";
    private static final String ADD_NO_SURE = "添加缓存失败,请检查Redis是否链接成功!";
    private static final String DELL_NO_SURE = "删除缓存失败,请检查Redis是否链接成功!";
    private final JTextField hostText;
    private final JTextField portText;
    private final JTextField inputRedisKey;
    private final JTextField inputRedisValue;
    private final JTextField inputRedisValueTimeOut;
    private final JTextField inputRedisDB;
    private final JTextArea jTextArea;
    private final JPasswordField passwordText;
    private final JRadioButton jRadioButton1;
    private final ButtonGroup jRadioButtonGroup;
    private final JFrame frame;
    private Jedis jedis = new Jedis();

    public static void main(String[] args) {
        //启动可视化界面
        RedisClientUI ui = new RedisClientUI();
    }

    public RedisClientUI() {
        //初始化用户之前保存的主机和密码
        String hostName = null;
        String portName = null;
        String password = null;

        File file = new File("D:\\App\\JavaXiuRedis\\user\\user.txt");
        try {
            if (file.exists()) {
                //读取文件内容
                BufferedReader br = new BufferedReader(new FileReader(file));
                try {
                    //读取保存的用户名和密码
                    String line = br.readLine();
                    if (StringUtils.isNotEmpty(line)) {
                        hostName = line.substring(0, line.indexOf('+'));
                        portName = line.substring(line.indexOf('+') + 1, line.indexOf('-'));
                        password = line.substring(line.indexOf('-') + 1);
                    }
                    log.info("初始化用户之前保存的主机和密码为：" + line);
                } catch (IOException ex) {
                    log.error("初始化用户之前保存的主机和密码出现异常...", ex);
                }
            } else {
                try {
                    boolean userMkdirs = new File("D:\\App\\JavaXiuRedis\\user").mkdirs();
                    log.error("生成用户信息文件夹是否成功-" + userMkdirs);
                    Writer userOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\App\\JavaXiuRedis\\user\\user.txt"), "GB2312"));
                    userOut.write("");
                    userOut.close();
                } catch (IOException e) {
                    log.error("文件不存在就生成该文件失败...", e);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("验证文件不存在！！！", e);
        }

        //构造一个新的JFrame，作为新窗口
        frame = new JFrame("新窗口");

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();

        JLabel jlb1 = new JLabel("地址:");
        JLabel jlb2 = new JLabel("端口:");
        JLabel jlb3 = new JLabel("密码:");
        JLabel jlbRedisKey = new JLabel("键名:");
        JLabel jlbRedisValue = new JLabel("键值:");
        JLabel jlbRedisValueTimeOut = new JLabel("秒:");
        JLabel jlbRedisDB = new JLabel("DB:");
        JLabel jlbAuth = new JLabel("授权");
        hostText = new JTextField(hostName, 10);
        portText = new JTextField(portName, 10);
        passwordText = new JPasswordField(password, 10);
        jRadioButton1 = new JRadioButton("Y");
        JRadioButton jRadioButton2 = new JRadioButton("N");
        jRadioButtonGroup = new ButtonGroup();
        JButton connButton = new JButton("链接");

        inputRedisKey = new JTextField("javaxiu.com", 10);
        inputRedisValue = new JTextField("javaxiu.com", 10);
        inputRedisValueTimeOut = new JTextField("", 3);
        inputRedisDB = new JTextField("", 3);
        JButton addButton = new JButton("添加");
        JButton delButton = new JButton("删除");

        jTextArea = new JTextArea("Redis查询结果...", 20, 95);
        JButton queryButton = new JButton("获取");
        JButton cancelButton = new JButton("关闭");

        jRadioButtonGroup.add(jRadioButton1);
        jRadioButtonGroup.add(jRadioButton2);
        //默认Y被选中
        jRadioButton1.setSelected(true);
        jRadioButton2.setSelected(false);

        //设置文本域中的文本为自动换行
        jTextArea.setLineWrap(true);
        //修改字体样式
        jTextArea.setFont(new Font("楷体", Font.PLAIN, 14));
        //增加滚动条显示
        JScrollPane jScrollPane = new JScrollPane(jTextArea);

        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.CENTER);
        frame.add(panel4, BorderLayout.SOUTH);

        panel1.add(jlb1);
        panel1.add(hostText);
        panel1.add(jlb2);
        panel1.add(portText);
        panel1.add(jlb3);
        panel1.add(passwordText);
        panel1.add(jlbAuth);
        panel1.add(jRadioButton1);
        panel1.add(jRadioButton2);
        panel1.add(connButton);

        //这里按钮和输入框是有顺序的
        panel2.add(jlbRedisKey);
        panel2.add(inputRedisKey);
        panel2.add(jlbRedisValue);
        panel2.add(inputRedisValue);
        panel2.add(jlbRedisValueTimeOut);
        panel2.add(inputRedisValueTimeOut);
        panel2.add(jlbRedisDB);
        panel2.add(inputRedisDB);
        panel2.add(addButton);
        panel2.add(delButton);

        //替换成滚动条显示
        //panel2.add(jTextArea);
        panel2.add(jScrollPane);
        panel2.add(queryButton);
        //panel3.add(queryButton);
        panel4.add(cancelButton);

        //设置窗体属性
        //设置窗体标题
        frame.setTitle("JavaXiuRedis客户端");
        //设置窗体大小
        frame.setSize(700, 520);
        //设置窗体初始位置
        frame.setLocation(520, 300);
        //设置窗体是否可见
        frame.setVisible(true);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                //DISPOSE_ON_CLOSE(在WindowConstants中定义)：调用任意已注册WindowListener的对象后自动隐藏并释放该窗体
                frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                log.info("JavaXiuRedis客户端程序退出...");
                //退出整个程序，如果有多个窗口，全部都销毁退出
                //System.exit(0);
                if (jedis != null) {
                    jedis.close();
                }
            }
        });

        connButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hostName = hostText.getText();
                String portName = portText.getText();
                String password = new String(passwordText.getPassword());
                //保存用户主机、端口、密码信息
                File file = new File("D:\\App\\JavaXiuRedis\\user\\user.txt");
                try {
                    FileWriter txt = new FileWriter(file);
                    txt.write(hostName + "+");
                    txt.write(portName + "-");
                    txt.write(password);
                    txt.close();
                } catch (IOException ex) {
                    log.error("保存用户主机和密码信息出现异常...", ex);
                }
                if (StringUtils.isBlank(hostName)) {
                    Messages.showMessageDialog("Redis主机-IP不能为空！", "错误...", Messages.getErrorIcon());
                    return;
                }
                if (StringUtils.isBlank(portName)) {
                    Messages.showMessageDialog("Redis主机-端口不能为空！", "错误...", Messages.getErrorIcon());
                    return;
                }
                boolean isAuth = false;
                if (jRadioButtonGroup.getSelection() == jRadioButton1.getModel()) {
                    if (StringUtils.isBlank(password)) {
                        Messages.showMessageDialog("Redis主机-授权模式-密码不能为空！", "错误...", Messages.getErrorIcon());
                        return;
                    }
                    isAuth = true;
                }
                //转换用户输入的端口号类型
                int portNames = 0;
                try {
                    portNames = Integer.parseInt(portName);
                } catch (NumberFormatException numberFormatException) {
                    log.error(PORT_NO_SURE, numberFormatException);
                    Messages.showMessageDialog(PORT_NO_SURE, "错误...", Messages.getErrorIcon());
                    return;
                }

                jedis = new Jedis(hostName, portNames);
                if (isAuth) {
                    try {
                        jedis.auth(password);
                        log.info("设置Redis密码并连接Redis成功！");
                        Messages.showMessageDialog(CONNECT_SUCCESS, "信息...", Messages.getInformationIcon());
                    } catch (Exception exception) {
                        log.error(PORT_NO_SURE, exception);
                        Messages.showMessageDialog(PASSWORD_NO_SURE, "错误...", Messages.getErrorIcon());
                    }
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputRedisValue.setEditable(true);
                String redisKey = inputRedisKey.getText();
                String redisValue = inputRedisValue.getText();
                String redisValueTimeOut = inputRedisValueTimeOut.getText();
                String redisDB = inputRedisDB.getText();
                if (StringUtils.isBlank(redisKey)) {
                    Messages.showMessageDialog("Redis-Key不能为空！", "错误...", Messages.getErrorIcon());
                    return;
                }
                if (StringUtils.isBlank(redisValue)) {
                    Messages.showMessageDialog("Redis-Value不能为空！", "错误...", Messages.getErrorIcon());
                    return;
                }
                if (jedis == null) {
                    Messages.showMessageDialog(ERROR, "错误...", Messages.getErrorIcon());
                    return;
                }
                //往redis中增加数据(设置库为db1)
                if (StringUtils.isNoneBlank(redisDB)) {
                    try {
                        jedis.select(Integer.parseInt(redisDB));
                    } catch (NumberFormatException numberFormatException) {
                        log.error(INDEX_NO_SURE, numberFormatException);
                        Messages.showMessageDialog(INDEX_NO_SURE, "错误...", Messages.getErrorIcon());
                        return;
                    }
                }
                //设置过期时间
                if (StringUtils.isBlank(redisValueTimeOut)) {
                    try {
                        jedis.set(redisKey, redisValue);
                    } catch (Exception exception) {
                        log.error(ADD_NO_SURE, exception);
                        Messages.showMessageDialog(ADD_NO_SURE, "错误...", Messages.getErrorIcon());
                        return;
                    }
                } else {
                    try {
                        //NX是不存在时才set,XX是存在时才set,EX是秒,PX是毫秒
                        jedis.set(redisKey, redisValue, "NX", "EX", Long.parseLong(redisValueTimeOut));
                    } catch (NumberFormatException numberFormatException) {
                        log.error(TIME_OUT_NO_SURE, numberFormatException);
                        Messages.showMessageDialog(TIME_OUT_NO_SURE, "错误...", Messages.getErrorIcon());
                        return;
                    }
                }
                Messages.showMessageDialog(SET_SUCCESS, "信息...", Messages.getInformationIcon());
            }
        });

        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //修改设置inputRedisValue为true
                inputRedisValue.setEditable(true);
                String redisKey = inputRedisKey.getText();
                String redisDB = inputRedisDB.getText();
                if (StringUtils.isBlank(redisKey)) {
                    Messages.showMessageDialog("Redis-Key不能为空！", "错误...", Messages.getErrorIcon());
                    return;
                }
                if (jedis == null) {
                    Messages.showMessageDialog(ERROR, "错误...", Messages.getErrorIcon());
                    return;
                }
                //从redis中删除数据(设置库为db1)
                if (StringUtils.isNoneBlank(redisDB)) {
                    try {
                        jedis.select(Integer.parseInt(redisDB));
                    } catch (NumberFormatException numberFormatException) {
                        log.error(INDEX_NO_SURE, numberFormatException);
                        Messages.showMessageDialog(INDEX_NO_SURE, "错误...", Messages.getErrorIcon());
                        return;
                    }
                }
                try {
                    jedis.del(redisKey);
                } catch (Exception exception) {
                    log.error(DELL_NO_SURE, exception);
                    Messages.showMessageDialog(DELL_NO_SURE, "错误...", Messages.getErrorIcon());
                    return;
                }
                Messages.showMessageDialog(DELETE_SUCCESS, "信息...", Messages.getInformationIcon());
            }
        });

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //修改设置inputRedisValue为true
                inputRedisValue.setEditable(true);
                String redisKey = inputRedisKey.getText();
                String redisDB = inputRedisDB.getText();
                if (StringUtils.isBlank(redisKey)) {
                    Messages.showMessageDialog("Redis-Key不能为空！", "错误...", Messages.getErrorIcon());
                    return;
                }
                if (jedis == null) {
                    Messages.showMessageDialog(ERROR, "错误...", Messages.getErrorIcon());
                    return;
                }

                //从redis中获取数据(设置库为db1)
                if (StringUtils.isNoneBlank(redisDB)) {
                    try {
                        jedis.select(Integer.parseInt(redisDB));
                    } catch (NumberFormatException numberFormatException) {
                        log.error(INDEX_NO_SURE, numberFormatException);
                        Messages.showMessageDialog(INDEX_NO_SURE, "错误...", Messages.getErrorIcon());
                        return;
                    }
                }
                Set<String> keys = new HashSet<>();
                Map<String, Object> values = new HashMap<>();
                //游标初始值为0
                String cursor = ScanParams.SCAN_POINTER_START;
                ScanParams scanParams = new ScanParams();
                //匹配以redisKey为前缀的key,1000个遍历一次
                scanParams.match(redisKey);
                scanParams.count(1000);

                do {
                    //使用scan命令获取数据,使用cursor游标记录位置,下次循环使用
                    ScanResult<String> scanResult;
                    try {
                        scanResult = jedis.scan(cursor, scanParams);
                        //返回0时说明遍历完成
                        cursor = scanResult.getStringCursor();
                        keys.addAll(new HashSet<>(scanResult.getResult()));
                    } catch (Exception ex) {
                        log.error(ERROR, ex);
                        Messages.showMessageDialog(ERROR, "错误...", Messages.getErrorIcon());
                        return;
                    }
                } while (!"0".equals(cursor));

                //如果输入的redisKey是模糊查询全量数据则只获取键值
                int keysSize = keys.size();
                if (keysSize > 100) {
                    values.put(redisKey, "[模糊查询全量数据过大时只获取所有的键...]");
                } else {
                    try {
                        for (String keyTmp : keys) {
                            String keyType = jedis.type(keyTmp);
                            switch (keyType) {
                                case "none":
                                    break;
                                case "list":
                                    values.put(keyTmp, jedis.lrange(keyTmp, 0, -1));
                                    break;
                                case "set":
                                    values.put(keyTmp, jedis.smembers(keyTmp));
                                    break;
                                case "string":
                                    values.put(keyTmp, jedis.get(keyTmp));
                                    break;
                                case "zset":
                                    values.put(keyTmp, jedis.zrange(keyTmp, 0, -1));
                                    break;
                                case "hash":
                                    values.put(keyTmp, jedis.hgetAll(keyTmp));
                                    break;
                            }
                        }
                    } catch (Exception ex) {
                        log.error(ERROR, ex);
                        Messages.showMessageDialog(ERROR, "错误...", Messages.getErrorIcon());
                        return;
                    }
                }

                //封装查询出的数据
                jTextArea.setText("【过期时间】：" + jedis.ttl(redisKey)
                        + "\r\n" + "【获取总数】：" + keysSize
                        + "\r\n" + "【获取的键】：" + keys
                        + "\r\n" + "【获取的值】：" + values);
                jTextArea.setEditable(true);
            }
        });

    }
}
