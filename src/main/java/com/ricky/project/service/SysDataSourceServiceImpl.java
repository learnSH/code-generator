package com.ricky.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ricky.project.domain.SysDataSource;
import com.ricky.project.mapper.SysDataSourceMapper;

/**
 * 系统数据源 业务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysDataSourceServiceImpl implements ISysDataSourceService
{
    @Autowired
    private SysDataSourceMapper mapper;

    /**
     * 查询系统数据源信息
     * 
     * @return 数据源配置
     */
    @Override
    public SysDataSource selectSysDataSource() {
    	return mapper.selectSysDataSource();
    }
    
    /**
     * 新增数据源配置信息
     * 
     * @param sysDataSource 数据源配置信息
     * @return 结果
     */
    @Override
    public int insertSysDataSource(SysDataSource sysDataSource) {
    	return mapper.insertSysDataSource(sysDataSource);
    }
    
    /**
     * 更新系统数据源信息
     * 
     * @param sysDataSource 系统数据源信息
     * @return 结果
     */
    @Override
    public int updateSysDataSource(SysDataSource sysDataSource) {
    	return mapper.updateSysDataSource(sysDataSource);
    }
}
