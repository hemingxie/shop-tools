package com.xingmima.dpfx.thread;

import com.xingmima.dpfx.dao.DdsrDao;
import com.xingmima.dpfx.dao.RateDao;
import com.xingmima.dpfx.dao.ShopDao;
import com.xingmima.dpfx.entity.DDsr;
import com.xingmima.dpfx.entity.DRated;
import com.xingmima.dpfx.entity.DShop;
import com.xingmima.dpfx.kafka.KafkaProperties;
import com.xingmima.dpfx.parser.ShopInfo;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * xingmima.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 *
 * @author tiaotiaohu
 * @version ShopInfoThread, v 0.1
 * @date 2016/8/31 20:37
 */
public class ShopInfoThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ShopInfoThread.class);
    //消息流
    private KafkaStream stream;
    //数据库操作
    private ShopDao sd = null;
    private RateDao rd = null;
    private DdsrDao dd = null;

    public ShopInfoThread(KafkaStream stream) {
        this.stream = stream;
        this.sd = new ShopDao();
        this.rd = new RateDao();
        this.dd = new DdsrDao();
    }

    @Override
    public void run() {
        ConsumerIterator<String, String> it = stream.iterator();
        while (it.hasNext()) {
            MessageAndMetadata<String, String> c = it.next();
            log.info("----------{}-----------@startup", KafkaProperties.TOPIC_SHOP_INFO);
            /*捕获异常，继续处理*/
            ShopInfo info = new ShopInfo(c.message()).call();
            if (null != info) {
                try {
                    DShop obj = info.handleShopInfo();
                    sd.insert(obj);
                } catch (Exception e) {
                    log.error(KafkaProperties.TOPIC_SHOP_INFO + ":", e);
                }
                try {
                    DRated obj = info.handleRating();
                    rd.insert(obj);
                } catch (Exception e) {
                    log.error(KafkaProperties.TOPIC_SHOP_INFO + ":", e);
                }
                try {
                    DDsr obj = info.handelDsr();
                    dd.insert(obj);
                } catch (Exception e) {
                    log.error(KafkaProperties.TOPIC_SHOP_INFO + ":", e);
                }
            }
        }
    }
}