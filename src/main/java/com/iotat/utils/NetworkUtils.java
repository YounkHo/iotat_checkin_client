package com.iotat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {

    private static final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);

    /**
     * 根据默认网关获取路由器的IP
     * @param gatewayIP
     * @return
     */
    public static String getRouterMACAddress(String gatewayIP){
        try {
            Process pro = Runtime.getRuntime().exec("arp -a");
            InputStreamReader isr = new InputStreamReader(pro.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            while(str!=null){
                String[] ipAndMac = str.trim().split("\\s+");
                if(ipAndMac.length >= 2 && ipAndMac[0].equals(gatewayIP)){
                    logger.debug("Successfully get your Router mac via arp.exe: [{}]", ipAndMac[1]);
                    return ipAndMac[1];
                }
                str = br.readLine();
            }
            br.close();
            isr.close();
        } catch (IOException e) {
            logger.error("Error occur! An IOException has occured.", e);
        }
        logger.error("Cannot get your Router mac and return default [{}]", "00-00-00-00-00-00");
        return "00-00-00-00-00-00";
    }

    /**
    * 获取本机MAC地址的方法  
    * @return
    */
    public static String getMACAddress(){  
        StringBuffer sb = new StringBuffer();  
        try{
            InetAddress ia1 = InetAddress.getLocalHost();//获取本地IP对象
            String ip = ia1.getHostAddress().toString(); //获取本机IP
            logger.debug("Your local ip is [{}]", ip);
            //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。  
            byte[] mac = NetworkInterface.getByInetAddress(ia1).getHardwareAddress();  
        
            //下面代码是把mac地址拼装成String  
            for(int i=0;i<mac.length;i++){  
                if(i!=0){  
                    sb.append("-");  
                }  
                //mac[i] & 0xFF 是为了把byte转化为正整数  
                String s = Integer.toHexString(mac[i] & 0xFF);  
                sb.append(s.length()==1?0+s:s);  
            }  
        }catch(UnknownHostException e){
            logger.error("Error occur! An IOException has occured.", e);
        }catch(SocketException e){
            logger.error("Error occur! An IOException has occured.", e);
        }
        logger.debug("Successfully get your local mac via eth0: [{}]", sb.toString().toUpperCase());
        //把字符串所有小写字母改为大写成为正规的mac地址并返回  
        return sb.toString().toUpperCase();  
    }

    /**
     * 获取默认网关IP
     * @return
     */
    public static String getGatewayIP(){
        try {
            Process pro = Runtime.getRuntime().exec("ipconfig");
            InputStreamReader isr = new InputStreamReader(pro.getInputStream(), "GBK");
            BufferedReader br = new BufferedReader(isr);
            
            String str = br.readLine();
            while(str!=null){
                String gatewatsIP[] = str.trim().split("(. )+:");
                if(gatewatsIP[0].trim().equals("默认网关") 
                    && !gatewatsIP[1].trim().equals("0.0.0.0")
                    && !gatewatsIP[1].trim().equals(null)){
                        logger.debug("Successfully get your default gateway ip: [{}]", gatewatsIP[1].trim());
                        return gatewatsIP[1].trim();
                    }
                str = br.readLine();
            }
            logger.error("Error occur! please check you network connection or set cmd encoding to GBK");
            br.close();
            isr.close();
        } catch (IOException e) {
            logger.error("Error occur! An IOException has occured.", e);
        }
        logger.error("Cannot get your gateway and return default [{}]", "0.0.0.0");
        return "0.0.0.0";
    }
}
