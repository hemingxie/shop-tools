# -*- coding: utf-8 -*-
import MySQLdb
import logging

logger = logging.getLogger(__name__)


class TaobaoShopMySQLAccess:

    def __init__(self,spider_node ,db_host, db_port, db_user, db_passwd, db_name):
        self.spider_node = spider_node
        self.db_host = db_host
        self.db_port = db_port
        self.db_user = db_user
        self.db_passwd = db_passwd
        self.db_name = db_name

    def load_shop_urls(self, shop_type):
        results = []
        conn = MySQLdb.connect(
            host=self.db_host,
            user=self.db_user,
            passwd=self.db_passwd,
            db=self.db_name,
            port=self.db_port,
        )
        try:
            cursor = conn.cursor()
            cursor.execute(
                'select shopid,store_url from t_shop where status = 0 and type="%s"' % shop_type)
            table = cursor.fetchall()
            for row in table:
                results.append(
                    {'shop_id': str(row[0]), 'url': str(row[1])})
            cursor.close()
        except MySQLdb.Error, e:
            logger.fatal("%d:%s" % (e.args[0], e.args[1]))
        conn.close()
        return results

    def inster_spider_job(self, runid, spider_name, logfile):
        conn = MySQLdb.connect(
            host=self.db_host,
            user=self.db_user,
            passwd=self.db_passwd,
            db=self.db_name,
            port=self.db_port,
        )
        try:
            cursor = conn.cursor()
            values = [runid, self.spider_node ,spider_name, logfile]
            cursor.execute(
                'insert into t_job (runid,spider_node_name,starttime,spider_name,logfile)values(%s,%s,now(),%s,%s)', values)
            cursor.close()
            conn.commit()
        except MySQLdb.Error, e:
            logger.fatal("%d:%s" % (e.args[0], e.args[1]))

    def update_spider_job(self, runid, spider_name, stats):
        conn = MySQLdb.connect(
            host=self.db_host,
            user=self.db_user,
            passwd=self.db_passwd,
            db=self.db_name,
            port=self.db_port,
        )
        try:
            cursor = conn.cursor()
            values = [stats, runid, spider_name, self.spider_node]
            cursor.execute(
                'update t_job set finishtime=now(),stats="%s" where runid=%s and spider_name=%s and spider_node_name=%s', values)
            conn.commit()
            cursor.close()
        except MySQLdb.Error, e:
            logger.fatal("%d:%s" % (e.args[0], e.args[1]))
