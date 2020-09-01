package com.ricky.project.service;

import com.ricky.project.domain.SysDataSource;

/**
 * 系统数据源 业务层
 * 
 * @author ruoyi
 */
public interface ISysDataSourceService
{
	/**
     * 查询数据源配置信息
     * 
     * @return 系统数据源
     */
    public SysDataSource selectSysDataSource();
    
    /**
     * 新增数据源配置信息
     * 
     * @param sysDataSource 数据源配置信息
     * @return 结果
     */
    public int insertSysDataSource(SysDataSource sysDataSource);
    
    /**
     * 更新系统数据源信息
     * 
     * @param sysDataSource 系统数据源信息
     * @return 结果
     */
    public int updateSysDataSource(SysDataSource sysDataSource);
}
