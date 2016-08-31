package com.xmm.spider.webapi.core;

import com.xmm.spider.webapi.configs.SpiderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Xingmima.com Inc.
 * Copyright (c) 2015-2016 All Rights Reserved.
 *
 * @author Huke
 * @version com.xmm.spider.webapi.core.Spider.java, v 0.1
 * @date 2016年8月30日 下午3:26:57
 */
public class Spider {
    private static final Logger LOGGER = LoggerFactory.getLogger(Spider.class);

    private SpiderConfig config;

    private Spider(SpiderConfig config) {
        this.config = config;
    }

    /**
     * Load spider.
     *
     * @param config the config
     * @return the spider
     */
    public static Spider load(SpiderConfig config) {
        Spider spider = new Spider(config);
        return spider;
    }

    /**
     * Get crawl list list.
     *
     * @return the list
     */
    public List<String> getCrawlList() {
        return exec("scrapy list");
    }

    /**
     * Get spider config list.
     *
     * @return the list
     */
    public String getSpiderConfig() {
        String content;
        File settingsFile = new File(config.getPath() + "/" + config.getName() + "/settings.py");
        if (settingsFile.exists()) {
            content = FileReader.Read(settingsFile);
        } else {
            content = "spider config not found!";
        }
        return content;
    }

    /**
     * Get spider log string.
     *
     * @param logFileName the log file name
     * @return the string
     */
    public String getSpiderLog(String logFileName) {
        String content;
        File logFile = new File(config.getLogPath() + "/" + logFileName);
        if (logFile.exists()) {
            content = FileReader.Read(logFile);
        } else {
            content = logFileName + " not found!";
        }
        return content;
    }

    /**
     * Run crawl string.
     *
     * @param name       the name
     * @return the string
     */
    public String runCrawl(String name) {
        String runID = getTimeHourString();
        exec(String.format("scrapy crawl %s --logfile=%s/%s-%s.log -a ri=%s", name, config.getLogPath(),name, runID, runID));
        return name + "-" + runID + ".log";
    }

    private List<String> exec(String cmd) {
        List<String> result = new ArrayList<>();
        try {
            File spiderPath = new File(this.config.getPath());
            if (spiderPath.exists()) {
                Process process = Runtime.getRuntime().exec(cmd, null, spiderPath);

                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    result.add(line);
                }
                input.close();
            } else {
                result.add("Spider not found!");
            }
        } catch (IOException e) {
            LOGGER.error(e.toString());
            result = new ArrayList<>();
            result.add(e.toString());
        }

        return result;
    }

    private static String getTimeHourString() {
        String result = "0";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = df.parse(df.format(calendar.getTime()));
            calendar.setTime(date);
            result = Long.toString(calendar.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
