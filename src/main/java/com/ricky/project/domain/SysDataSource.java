package com.ricky.project.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.ricky.framework.web.domain.BaseEntity;



/**
 * 系统数据源配置表 sys_config
 * 
 * @author ruoyi
 */
public class SysDataSource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 数据源主键 */
    private Long dataId;

    /** 名称 */
    private String name;

    /** 驱动类型*/
    private String driver;
    
    /** oracle连接方式*/
    private String oracleConnMode;
    
    /** oracle连接服务名或SID*/
    private String serviceNameOrSid;

    /** 主机 */
    private String host;
    
    /** 端口号 */
    private Integer port;
    
    /** 用户名 */
    private String username;
    
    /** 密码 */
    private String password;

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getOracleConnMode() {
		return oracleConnMode;
	}

	public void setOracleConnMode(String oracleConnMode) {
		this.oracleConnMode = oracleConnMode;
	}

	public String getServiceNameOrSid() {
		return serviceNameOrSid;
	}

	public void setServiceNameOrSid(String serviceNameOrSid) {
		this.serviceNameOrSid = serviceNameOrSid;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dataId", getDataId())
            .append("name", getName())
            .append("driver", getDriver())
            .append("oracleConnType", getOracleConnMode())
            .append("serviceNameOrSid", getServiceNameOrSid())
            .append("host", getHost())
            .append("port", getPort())
            .append("username", getUsername())
            .append("password", getPassword())
            .toString();
    }
}
