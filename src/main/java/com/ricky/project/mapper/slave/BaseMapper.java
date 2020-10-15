package com.ricky.project.mapper.slave;

import java.util.List;

import com.ricky.project.domain.GenTable;
import com.ricky.project.domain.GenTableColumn;

/**
 * 从库数据源 数据层
 * 
 * @author ricky
 */
public interface BaseMapper
{
    
    /**
     * 查询据库列表
     * 
     * @param genTable 业务信息
     * @return 数据库表集合
     */
    public List<GenTable> selectDbTableList(GenTable genTable);

    /**
     * 查询据库列表
     * 
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    public List<GenTable> selectDbTableListByNames(String[] tableNames);
    
    /**
     * 根据表名称查询列信息
     * 
     * @param tableName 表名称
     * @return 列信息
     */
    public List<GenTableColumn> selectDbTableColumnsByName(String tableName);
}