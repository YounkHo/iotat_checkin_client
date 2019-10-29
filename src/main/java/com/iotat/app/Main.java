package com.iotat.app;

import com.iotat.ui.Tray;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
        PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));
		//URL loggerConfig = ClassLoader.getSystemResource("log4j.properties");
		logger.debug("Program run.");
		Tray tray = new Tray();
		tray.runInTaskbar();
		logger.debug("Program exit.");
	}
	
	
}
