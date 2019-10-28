package com.iotat.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class NetworkUtils {

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
                if(ipAndMac.length >= 2 && ipAndMac[0].equals(gatewayIP))
                    return ipAndMac[1];
                str = br.readLine();
            }
            br.close();
            isr.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "00-00-00-00-00-00";
    }

    /**
    * 获取本机MAC地址的方法  
    * @return
    * @throws Exception
    */
    public static String getMACAddress()throws Exception{  
    	InetAddress ia1 = InetAddress.getLocalHost();//获取本地IP对象
        String ip = ia1.getHostAddress().toString();
        //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。  
        byte[] mac = NetworkInterface.getByInetAddress(ia1).getHardwareAddress();  
        
        //下面代码是把mac地址拼装成String  
        StringBuffer sb = new StringBuffer();  
        
        for(int i=0;i<mac.length;i++){  
            if(i!=0){  
                sb.append("-");  
            }  
            //mac[i] & 0xFF 是为了把byte转化为正整数  
            String s = Integer.toHexString(mac[i] & 0xFF);  
            
            sb.append(s.length()==1?0+s:s);  
        }  
        
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
                    && !gatewatsIP[1].trim().equals(null))
                    return gatewatsIP[1].trim();
                str = br.readLine();
            }
            br.close();
            isr.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "0.0.0.0";
    }
}
