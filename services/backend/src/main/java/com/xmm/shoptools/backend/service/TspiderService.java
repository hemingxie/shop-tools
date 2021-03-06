package com.xmm.shoptools.backend.service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xmm.shoptools.backend.dao.TspiderDao;
import com.xmm.shoptools.backend.entity.Tspider;
import com.xmm.shoptools.backend.utils.BeanVoUtil;
import com.xmm.shoptools.backend.utils.RandomUtil;
import com.xmm.shoptools.backend.utils.StringUtils;
import com.xmm.shoptools.backend.vo.PageResult;
import com.xmm.shoptools.backend.vo.TspiderQuery;
import com.xmm.shoptools.backend.vo.TspiderVO;

/**
 * @author auto created
 * @version 1.0
 * @since 1.0
 */
@Service
public class TspiderService{
    
    @Autowired
    private TspiderDao tspiderDao;
    
    public PageResult<TspiderVO> query(TspiderQuery query) {
        PageResult<TspiderVO> pr = new PageResult<TspiderVO>();

        // 添加符合条件的总记录数
        Integer total = this.tspiderDao.count(query);
        pr.setTotal(total);
        
        // 添加当前页显示的内容
        List<Tspider> rows = this.tspiderDao.query(query);
        List<TspiderVO> rowsVo = BeanVoUtil.copyList(rows, TspiderVO.class);
        pr.setRows(rowsVo);

        return pr;
    }

    public Tspider getTspider(String id) {
        if(id!=null){
            return tspiderDao.getWhitOutCache(id);
        }
        return null;
    }

    public void addTspider(String nodeName, String host, String descrition) {
        Tspider tspider = new Tspider();
        tspider.setId(RandomUtil.getUUID());
        tspider.setNodeName(nodeName.trim());
        tspider.setHost(host.trim());
        if(!StringUtils.isEmpty(descrition)){
            tspider.setDescrition(descrition.trim()); 
        }
        tspider.setEnable(true);
        tspider.setCreated(new Date());
        tspider.setUpdated(new Date());
        this.tspiderDao.insert(tspider);
    }

    public void editTspider(String id, String nodeName, String host, String descrition) {
        Tspider tspider = this.getTspider(id);
        if(tspider!=null){
            tspider.setNodeName(nodeName.trim());
            tspider.setHost(host.trim());
            if(!StringUtils.isEmpty(descrition)){
                tspider.setDescrition(descrition.trim()); 
            }
            tspider.setUpdated(new Date());
            this.tspiderDao.update(tspider);
        }
    }
    
    /*启用和禁用*/
    public boolean disableTspider(String id) {
        Tspider tspider = this.getTspider(id);
        if(tspider!=null&&tspider.getEnable()==true){
            tspider.setEnable(false);
            tspider.setUpdated(new Date());
            this.tspiderDao.update(tspider);
            return true;
        }else if (tspider!=null&&tspider.getEnable()==false) {
            tspider.setEnable(true);
            tspider.setUpdated(new Date());
            this.tspiderDao.update(tspider);
            return true;
        }
        return false;
    }
    
    /**
     * 通过节点名查找爬虫节点
     * @param nodeName
     * @return
     */
    public Tspider selectTspiderBynodeName(String nodeName) {
        if(!StringUtils.isEmpty(nodeName)){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("nodeName", nodeName);
            return this.tspiderDao.getWhitOutCache(map , "selectTspiderBynodeName");
        }
        return null;
    }
    
    /**
     * 查询所有非禁用的爬虫节点
     * @return
     */
    public List<Tspider> selectAllTspider() {
        return this.tspiderDao.selectAllTspider();
    }
    
    /**
     * 删除爬爬虫节点
     * @param id
     * @return
     */
    public boolean deleteTspider(String id) {
        Tspider tspider = this.getTspider(id);
        if(null!=tspider){
            this.tspiderDao.delete("deleteTspider", id);
            return true;
        }
        return false;
    }

}
