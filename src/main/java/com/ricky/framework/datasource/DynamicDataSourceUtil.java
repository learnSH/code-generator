package com.ricky.framework.datasource;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;
import com.ricky.common.utils.spring.SpringUtils;

/**
 * 动态数据源工具
 * 
 * @author ruoyi
 */
public class DynamicDataSourceUtil 
{
    public static Map<Object, Object> dataSourceMap = new HashMap<>();
 
    public static void flushDataSource() {
        //获取spring管理的dynamicDataSource
        DynamicDataSource dynamicDataSource = (DynamicDataSource) SpringUtils.getBean("dynamicDataSource");
        //将数据源设置到 targetDataSources
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        //将 targetDataSources 中的连接信息放入 resolvedDataSources 管理
        dynamicDataSource.afterPropertiesSet();
    }
 
    public static void addDataSource(String key, DruidDataSource masterDataSource) {
        dataSourceMap.put(key, masterDataSource);
    }
}