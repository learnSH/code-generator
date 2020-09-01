package com.ricky.project.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ricky.project.domain.SysDataSource;

/**
 * 数据源 数据层
 * 
 * @author ruoyi
 */
@Mapper
public interface SysDataSourceMapper
{
	/**
     * 查询数据源配置信息
     * 
     * @return 数据源配置信息
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
     * 更新数据源配置信息
     * 
     * @param sysDataSource 数据源配置信息
     * @return 结果
     */
    public int updateSysDataSource(SysDataSource sysDataSource);
}
