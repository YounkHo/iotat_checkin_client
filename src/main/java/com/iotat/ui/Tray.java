package com.iotat.ui;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.iotat.utils.NetworkUtils;

public class Tray {

	private TrayIcon trayIcon;
	
	public Tray() {
		postData();
	}
	
	public void runInTaskbar(){
        if (SystemTray.isSupported())
        {
            URL url = ClassLoader.getSystemResource("icon.png");
            System.out.println(ClassLoader.getSystemResource(""));
            ImageIcon icon = new ImageIcon(url);
            Image image = icon.getImage();
            trayIcon = new TrayIcon(image);
            trayIcon.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    if (e.getClickCount() == 2)
                        JOptionPane.showMessageDialog(null, "Welcome to IOTA Timer!");
                }
            });
            
            PopupMenu popupMenu = new PopupMenu();
            popupMenu.add(new MenuItem("禁用(D)"));
            popupMenu.add(new MenuItem("状态(S)"));
            popupMenu.add(new MenuItem("修复(P)"));
            popupMenu.addSeparator();
            popupMenu.add(new MenuItem("更改 Windows 防火墙设置(C)"));
            popupMenu.addSeparator();
            popupMenu.add(new MenuItem("exit"));

            trayIcon.setPopupMenu(popupMenu);
            SystemTray systemTray = SystemTray.getSystemTray();
            try{
                systemTray.add(trayIcon);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Your Computer don't support tray!");
        }
    }
	
	public void postData(){
        Timer postDataTimer = new Timer();
        postDataTimer.schedule(new TimerTask(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while(true){
                	try {
						String localMacAddress = NetworkUtils.getMACAddress();
                        String gatewayIP = NetworkUtils.getGatewayIP();
                        String remoteMacAddress = NetworkUtils.getRouterMACAddress(gatewayIP);
						String connectStatuString = "未连接";
						
						//TODO: add post code
						System.out.println(localMacAddress+"&"+remoteMacAddress);
						
						trayIcon.setToolTip("本机MAC：" + localMacAddress+"\r\n状态：" + connectStatuString);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
            
        }, 3*60*1000);
        
    }
}
