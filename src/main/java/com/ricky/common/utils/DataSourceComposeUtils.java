package com.ricky.common.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.ricky.common.constant.Constants;
import com.ricky.common.exception.BusinessException;
import com.ricky.project.domain.SysDataSource;

public class DataSourceComposeUtils {
	
	/**MySql连接字符串模板*/
	private static String URL_TEMPLATE_MYSQL = "jdbc:mysql://{}:{}/{}?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
	/**Oracle的SID连接字符串模板*/
	private static String URL_TEMPLATE_ORACLE_SID = "jdbc:oracle:thin:@{}:{}:{}";
	/**Oracle的服务名连接字符串模板*/
	private static String URL_TEMPLATE_ORACLE_SERVICE_NAME = "jdbc:oracle:thin:@{}:{}/{}";
	
	public static DruidDataSource composeDruidDataSource(SysDataSource dataSource) throws BusinessException{
    	DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(dataSource.getUsername());
        if (StringUtils.isNotEmpty(dataSource.getPassword())) {
        	druidDataSource.setPassword(dataSource.getPassword());
		}
        //判断数据库类型，可以继续 if else 判断是否是其他数据库
        if (StringUtils.equals(dataSource.getDriver(), Constants.DATABASE_DRIVER_MYSQL)) {
        	druidDataSource.setDriverClassName(Constants.DRIVER_CLASS_NAME_MYSQL);
            druidDataSource.setUrl(StringUtils.format(URL_TEMPLATE_MYSQL, dataSource.getHost(),dataSource.getPort(),dataSource.getName()));
		} else if (StringUtils.equals(dataSource.getDriver(), Constants.DATABASE_DRIVER_ORACLE)) {
			druidDataSource.setDriverClassName(Constants.DRIVER_CLASS_NAME_ORACLE);
			if (StringUtils.equals(dataSource.getOracleConnMode(), Constants.ORACLE_CONN_TYPE_SID)) {
				//如果是连接方式是SID
				druidDataSource.setUrl(StringUtils.format(URL_TEMPLATE_ORACLE_SID, dataSource.getHost(),dataSource.getPort(),dataSource.getServiceNameOrSid()));
			} else if (StringUtils.equals(dataSource.getOracleConnMode(), Constants.ORACLE_CONN_TYPE_SERVICE_NAME)) {
				//如果是连接方式是SID
				druidDataSource.setUrl(StringUtils.format(URL_TEMPLATE_ORACLE_SERVICE_NAME, dataSource.getHost(),dataSource.getPort(),dataSource.getServiceNameOrSid()));
			}
		} else {
			throw new BusinessException("数据库驱动类型无效");
		}
        return druidDataSource;
    }
}
