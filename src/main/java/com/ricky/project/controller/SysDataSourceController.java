package com.ricky.project.controller;

import java.sql.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.pool.DruidDataSource;
import com.ricky.common.exception.BusinessException;
import com.ricky.common.utils.DataSourceComposeUtils;
import com.ricky.framework.aspectj.lang.enums.DataSourceType;
import com.ricky.framework.datasource.DynamicDataSourceUtil;
import com.ricky.framework.web.controller.BaseController;
import com.ricky.framework.web.domain.AjaxResult;
import com.ricky.project.domain.SysDataSource;
import com.ricky.project.service.ISysDataSourceService;

/**
 * 系统数据源信息
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/dataSource")
public class SysDataSourceController extends BaseController
{
    private String prefix = "system/dataSource";

    @Autowired
    private ISysDataSourceService sysDataSourceService;

    /**
     * 设置系统配置
     */
    @GetMapping()
    public String config(ModelMap mmap)
    {
    	SysDataSource dataSource = sysDataSourceService.selectSysDataSource();
        mmap.put("dataSource", dataSource == null ? new SysDataSource() : dataSource);
        return prefix + "/config";
    }
    
    /**
     * 测试连接数据源
     */
	@PostMapping("/connect")
    @ResponseBody
    public AjaxResult connect(SysDataSource dataSource) {
    	DruidDataSource druidDataSource = DataSourceComposeUtils.composeDruidDataSource(dataSource);
        Connection connection = null;
        try {
        	//去掉连接失败重试
        	druidDataSource.setBreakAfterAcquireFailure(true);
        	druidDataSource.setConnectionErrorRetryAttempts(0);
        	connection = druidDataSource.getConnection(5000);
        	if (connection != null) {
        		connection.close();
        		return success("连接成功!");
			}
        	return error("连接失败!");
		} catch (Exception e) {
			return error("连接失败!");
		}
    }
    
    /**
     * 设置系统数据源（保存）
     */
    @PostMapping("/save")
    @ResponseBody
    public AjaxResult save(SysDataSource dataSource) throws BusinessException{
    	int rows= 0;
    	if (dataSource.getDataId() != null) {
    		rows= sysDataSourceService.updateSysDataSource(dataSource);
		} else {
			rows= sysDataSourceService.insertSysDataSource(dataSource);
		}
    	if (rows > 0) {
    		DruidDataSource druidDataSource = DataSourceComposeUtils.composeDruidDataSource(dataSource);
            //添加数据源
            DynamicDataSourceUtil.addDataSource(DataSourceType.SLAVE.name(), druidDataSource);
            //刷新数据源
            DynamicDataSourceUtil.flushDataSource();
		}
        return toAjax(rows);
    }
}
