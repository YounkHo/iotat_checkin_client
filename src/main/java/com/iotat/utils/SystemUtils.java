package com.iotat.utils;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SystemUtils
 */
public class SystemUtils {

    private static final Logger logger = LoggerFactory.getLogger(SystemUtils.class);
    private static final String LNK = "IOTAT_Checkin.lnk";

    // 写入快捷方式 是否自启动，快捷方式的名称，注意后缀是lnk
	public static boolean setAutoStart(boolean yesAutoStart) {
		File f = new File(LNK);
		String p = f.getAbsolutePath();
		String startFolder = "";
		String osName = System.getProperty("os.name");
		String str = System.getProperty("user.home");
		if (osName.equals("Windows 7") || osName.equals("Windows 8") || osName.equals("Windows 10")
				|| osName.equals("Windows Server 2012 R2") || osName.equals("Windows Server 2014 R2")
				|| osName.equals("Windows Server 2016")) {
			startFolder = str
					+ "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
		}
		if (osName.endsWith("Windows XP")) {
			startFolder = str + "\\「开始」菜单\\程序\\启动";
		}
		if (setRunBySys(yesAutoStart, p, startFolder, LNK)) {
            logger.debug("Set program autorun success.");
			return true;
		}
		return false;
	}

	// 设置是否随系统启动
	private static boolean setRunBySys(boolean b, String path, String path2, String lnk) {
		File file = new File(path2 + "\\" + lnk);
		Runtime run = Runtime.getRuntime();
		File f = new File(lnk);

		// 复制
		try {
			if (b) {
				// 写入
				// 判断是否隐藏，注意用系统copy布置为何隐藏文件不生效
				if (f.isHidden()) {
					// 取消隐藏
					try {
						Runtime.getRuntime().exec("attrib -H \"" + path + "\"");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (!file.exists()) {
					run.exec("cmd /c copy " + formatPath(path) + " " + formatPath(path2));
				}
				// 延迟0.5秒防止复制需要时间
				Thread.sleep(500);
			} else {
				// 删除
				if (file.exists()) {
					if (file.isHidden()) {
						// 取消隐藏
						try {
							Runtime.getRuntime().exec("attrib -H \"" + file.getAbsolutePath() + "\"");
						} catch (IOException e) {
							e.printStackTrace();
						}
						Thread.sleep(500);
					}
					run.exec("cmd /c del " + formatPath(file.getAbsolutePath()));
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 解决路径中空格问题
	private static String formatPath(String path) {
		if (path == null) {
			return "";
		}
		return path.replaceAll(" ", "\" \"");
    }
    
    /**
     * judge is program is autorun
     */
    public static boolean isAutoRun(){
        String str = System.getProperty("user.home");
        String startFolder = "";
		String osName = System.getProperty("os.name");
        if (osName.equals("Windows 7") || osName.equals("Windows 8") || osName.equals("Windows 10")
				|| osName.equals("Windows Server 2012 R2") || osName.equals("Windows Server 2014 R2")
				|| osName.equals("Windows Server 2016")) {
			startFolder = str
					+ "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
		}
		if (osName.endsWith("Windows XP")) {
			startFolder = str + "\\「开始」菜单\\程序\\启动";
        }
        File lnkFile = new File(startFolder + "\\" + LNK);
        if(lnkFile.exists())
            return true;
        else
            return false;
    }

}