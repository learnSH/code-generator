package com.ricky.project.mapper;

import java.util.List;

import com.ricky.project.domain.GenTable;

/**
 * 业务 数据层
 * 
 * @author ruoyi
 */
public interface GenTableMapper
{
    /**
     * 查询业务列表
     * 
     * @param genTable 业务信息
     * @return 业务集合
     */
    public List<GenTable> selectGenTableList(GenTable genTable);

    /**
     * 查询表名称业务信息
     * 
     * @param tableName 表名称
     * @return 业务信息
     */
    public GenTable selectGenTableByName(String tableName);
    
    /**
     * 查询所有表信息
     * 
     * @param dataSourceId	数据源主键
     * @return 表信息集合
     */
    public List<GenTable> selectGenTableAll(Long dataSourceId);
    
    /**
     * 查询表ID业务信息
     * 
     * @param id 业务ID
     * @return 业务信息
     */
    public GenTable selectGenTableById(Long id);

    /**
     * 新增业务
     * 
     * @param genTable 业务信息
     * @return 结果
     */
    public int insertGenTable(GenTable genTable);

    /**
     * 修改业务
     * 
     * @param genTable 业务信息
     * @return 结果
     */
    public int updateGenTable(GenTable genTable);

    /**
     * 批量删除业务
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGenTableByIds(Long[] ids);
}