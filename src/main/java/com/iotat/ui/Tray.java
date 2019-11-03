package com.iotat.ui;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.alibaba.fastjson.JSONObject;
import com.iotat.utils.HttpRequest;
import com.iotat.utils.NetworkUtils;
import com.iotat.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("AlibabaAvoidManuallyCreateThread")
public class Tray {

    private TrayIcon trayIcon;
    private static final Logger logger = LoggerFactory.getLogger(Tray.class);

    public Tray() {
        postData();
    }

    /**
     * set Program run in task bar
     */
    public void runInTaskbar() {
        if (SystemTray.isSupported()) {
            URL url = ClassLoader.getSystemResource("icon.png");
            ImageIcon icon = new ImageIcon(url);
            Image image = icon.getImage();
            trayIcon = new TrayIcon(image);
            trayIcon.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        JOptionPane.showMessageDialog(null, "Welcome to IOTA Timer!");
                        logger.info("This SB user has double-click the tray.");
                    }
                }
            });

            PopupMenu popupMenu = new PopupMenu();
            MenuItem autoStart = new MenuItem();
            if (SystemUtils.isAutoRun())
                autoStart.setLabel("Cancel Autorun");
            else
                autoStart.setLabel("Set Autorun");
            autoStart.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!SystemUtils.isAutoRun()) {
                        if (SystemUtils.setAutoStart(true)) {
                            autoStart.setLabel("Cancel Autorun");
                            logger.debug("SB User Successfully set the program auto start.");
                        } else
                            logger.error("SB User FAILED set the program auto start.");
                    }
                }
            });
            popupMenu.add(autoStart);
            popupMenu.addSeparator();

            MenuItem about = new MenuItem("About");
            about.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "开发组：\n\r谭本超: 1149284750@qq.com\n\r缪   玲: 2236103111@qq.com\n\r樊国一: 2398409722@qq.com");
                    logger.debug("User has view the developer.");
                }
            });
            popupMenu.add(about);
            popupMenu.addSeparator();

            MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.debug("SB User exit the program manually.");
                    System.exit(0);
                }
            });
            popupMenu.add(exit);
            trayIcon.setPopupMenu(popupMenu);

            SystemTray systemTray = SystemTray.getSystemTray();
            try {
                systemTray.add(trayIcon);
                logger.debug("Program has run in taskbar.");
            } catch (Exception e) {
                logger.error("Error occurred. Add system tray icon failed.", e);
            }
        } else {
            logger.error("Error occurred. Computer don't support tray.");
            JOptionPane.showMessageDialog(null, "Your computer don't support tray!");
        }
    }

    /**
     * get to Server every interval
     */
    public void postData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {

                    String localMacAddress = NetworkUtils.getMACAddress();
                    String gatewayIP = NetworkUtils.getGatewayIP();
                    String remoteMacAddress = NetworkUtils.getRouterMACAddress(gatewayIP);
                    String connectStatuString = "未连接";
                    logger.debug("Successfully get your local mac:[{}] and remote Router mac [{}]", localMacAddress,
                            remoteMacAddress);

                    logger.debug(localMacAddress + "&" + remoteMacAddress);

                    String response = HttpRequest.sendGet("http://10.10.5.130:18887/online",
                    "selfMac="+localMacAddress + "&commonMac=" + remoteMacAddress);
                    JSONObject responseJson = JSONObject.parseObject(response);
                    logger.debug("Server reponse [{}]", response);
                    connectStatuString = responseJson.getString("message");
                    trayIcon.setToolTip("本机MAC：" + localMacAddress + "\r\n状态：" + connectStatuString);
                    
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted Exception occurred.", e);
                    }
                }
            }
        }).start();// This sets program send router mac to server every 3 minutes

    }
}
