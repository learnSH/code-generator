package com.ricky.project.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ricky.common.constant.Constants;
import com.ricky.common.constant.GenConstants;
import com.ricky.common.exception.BusinessException;
import com.ricky.common.utils.StringUtils;
import com.ricky.common.utils.file.FileUtils;
import com.ricky.common.utils.text.CharsetKit;
import com.ricky.common.utils.text.Convert;
import com.ricky.framework.aspectj.lang.enums.DataSourceType;
import com.ricky.framework.datasource.DynamicDataSourceContextHolder;
import com.ricky.project.domain.GenTable;
import com.ricky.project.domain.GenTableColumn;
import com.ricky.project.domain.SysConfig;
import com.ricky.project.domain.SysDataSource;
import com.ricky.project.mapper.GenTableColumnMapper;
import com.ricky.project.mapper.GenTableMapper;
import com.ricky.project.mapper.SysConfigMapper;
import com.ricky.project.mapper.SysDataSourceMapper;
import com.ricky.project.mapper.slave.BaseMapper;
import com.ricky.project.mapper.slave.MySQLMapper;
import com.ricky.project.mapper.slave.OracleMapper;
import com.ricky.project.mapper.slave.PostgreSQLMapper;
import com.ricky.project.mapper.slave.SQLServerMapper;
import com.ricky.project.util.GenUtils;
import com.ricky.project.util.VelocityInitializer;
import com.ricky.project.util.VelocityUtils;

/**
 * 业务 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class GenTableServiceImpl implements IGenTableService
{
    private static final Logger log = LoggerFactory.getLogger(GenTableServiceImpl.class);

    @Autowired
    private GenTableMapper genTableMapper;

    @Autowired
    private GenTableColumnMapper genTableColumnMapper;

    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private SysDataSourceMapper dataSourceMapper;
    
    @Autowired
    private MySQLMapper mysqlMapper;
    @Autowired
    private OracleMapper oracleMapper;
    @Autowired
    private PostgreSQLMapper postgresqlMapper;
    @Autowired
    private SQLServerMapper sqlserverMapper;
    
    /**
     * 查询业务信息
     * 
     * @param id 业务ID
     * @return 业务信息
     */
    @Override
    public GenTable selectGenTableById(Long id)
    {
        GenTable genTable = genTableMapper.selectGenTableById(id);
        setTableFromOptions(genTable);
        return genTable;
    }

    /**
     * 查询业务列表
     * 
     * @param genTable 业务信息
     * @return 业务集合
     */
    @Override
    public List<GenTable> selectGenTableList(GenTable genTable)
    {
        return genTableMapper.selectGenTableList(genTable);
    }

    /**
     * 根据数据库类型获取对应的mapper
     * 
     * @param dbType 数据库类型
     * @return
     */
    private BaseMapper getSlaveMapper(String dbType) 
    {
    	if (Constants.DATABASE_TYPE_MYSQL.equals(dbType)) 
    	{
			return mysqlMapper;
		} 
    	else if (Constants.DATABASE_TYPE_ORACLE.equals(dbType)) 
		{
			return oracleMapper;
		}
    	else if (Constants.DATABASE_TYPE_SQLSERVER.equals(dbType)) 
		{
			return sqlserverMapper;
		}
    	else if (Constants.DATABASE_TYPE_POSTGRESQL.equals(dbType)) 
		{
			return postgresqlMapper;
		}
    	return mysqlMapper;
    }
    
    /**
     * 根据表名获取列
     * 
     * @param dataSourceId	数据源主键
     * @param tableName		表名
     * @return
     */
    private List<GenTableColumn> selectDbTableColumnsByName(Long dataSourceId, String tableName)
    {
    	SysDataSource dataSource = dataSourceMapper.selectSysDataSource(dataSourceId);
    	try {
    		//切换数据源
        	DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE.name() + Convert.toStr(dataSource.getId()));
        	return getSlaveMapper(dataSource.getDbType()).selectDbTableColumnsByName(tableName);
		} catch (Exception e) {
			// 有异常不抛出，返回null，防止无法回滚
			return null;
		} finally {
			DynamicDataSourceContextHolder.clearDataSourceType();
		}
    }
    
    /**
     * 查询据库列表
     * 
     * @param genTable 	业务信息
     * @param dbType	数据库类型
     * @return 数据库表集合
     */
    public List<GenTable> selectDbTableList(GenTable genTable)
    {
    	try {
    		SysDataSource dataSource = dataSourceMapper.selectSysDataSource(genTable.getDataSourceId());
        	
        	//切换数据源
        	DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE.name() + Convert.toStr(dataSource.getId()));
        	return getSlaveMapper(dataSource.getDbType()).selectDbTableList(genTable);
		} finally {
			DynamicDataSourceContextHolder.clearDataSourceType();
		}
    }

    /**
     * 查询据库列表
     * 
     * @param tableNames 表名称组
     * @param dataSourceId	数据源主键
     * @return 数据库表集合
     */
    public List<GenTable> selectDbTableListByNames(String[] tableNames,Long dataSourceId)
    {
    	SysDataSource dataSource = dataSourceMapper.selectSysDataSource(dataSourceId);
    	try {
    		//切换数据源
        	DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.SLAVE.name() + Convert.toStr(dataSource.getId()));
        	return getSlaveMapper(dataSource.getDbType()).selectDbTableListByNames(tableNames);
		} finally {
			DynamicDataSourceContextHolder.clearDataSourceType();
		}
    }

    /**
     * 查询所有表信息
     * 
     * @return 表信息集合
     */
    @Override
    public List<GenTable> selectGenTableAll()
    {
        return genTableMapper.selectGenTableAll();
    }
    
    /**
     * 修改业务
     * 
     * @param genTable 业务信息
     * @return 结果
     */
    @Override
    @Transactional
    public void updateGenTable(GenTable genTable)
    {
        String options = JSON.toJSONString(genTable.getParams());
        genTable.setOptions(options);
        int row = genTableMapper.updateGenTable(genTable);
        if (row > 0)
        {
            for (GenTableColumn cenTableColumn : genTable.getColumns())
            {
                genTableColumnMapper.updateGenTableColumn(cenTableColumn);
            }
        }
    }

    /**
     * 删除业务对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional
    public void deleteGenTableByIds(String ids)
    {
        genTableMapper.deleteGenTableByIds(Convert.toLongArray(ids));
        genTableColumnMapper.deleteGenTableColumnByIds(Convert.toLongArray(ids));
    }

    /**
     * 导入表结构
     * 
     * @param tableList 导入表列表
     * @param dataSourceId	数据源主键
     */
    @Override
    public void importGenTable(List<GenTable> tableList, Long dataSourceId)
    {
    	SysConfig config = configMapper.selectSysConfig();
        String operName = "管理员";
        SysDataSource dataSource = dataSourceMapper.selectSysDataSource(dataSourceId);
        for (GenTable table : tableList)
        {
            try
            {
                String tableName = table.getTableName();
                GenUtils.initTable(table, operName,config);
                table.setDataSourceId(dataSourceId);
                int row = genTableMapper.insertGenTable(table);
                if (row > 0)
                {
                	List<GenTableColumn> dbTableColumns = selectDbTableColumnsByName(dataSource.getId(),tableName);
                    for (GenTableColumn column : dbTableColumns)
                    {
                        GenUtils.initColumnField(column, table);
                        genTableColumnMapper.insertGenTableColumn(column);
                    }
                }
            }
            catch (Exception e)
            {
                log.error("表名 " + table.getTableName() + " 导入失败：", e);
            }
        }
    }

    /**
     * 预览代码
     * 
     * @param tableId 表编号
     * @return 预览数据列表
     */
    public Map<String, String> previewCode(Long tableId)
    {
    	Map<String, String> dataMap = new LinkedHashMap<>();
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableById(tableId);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);
        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            dataMap.put(template, sw.toString());
        }
        return dataMap;
    }

    /**
     * 生成代码（下载方式）
     * 
     * @param tableName 表名称
     * @return 数据
     */
    @Override
    public byte[] downloadCode(String tableName)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(tableName, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 生成代码（自定义路径）
     * 
     * @param tableName 表名称
     */
    @Override
    public void generatorCode(String tableName)
    {
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates)
        {
            if (!StringUtils.contains(template, "sql.vm"))
            {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF8);
                tpl.merge(context, sw);
                try
                {
                    String path = getGenPath(table, template);
                    FileUtils.writeStringToFile(new File(path), sw.toString(), CharsetKit.UTF_8);
                }
                catch (IOException e)
                {
                    throw new BusinessException("渲染模板失败，表名：" + table.getTableName());
                }
            }
        }
    }

    /**
     * 同步数据库
     * 
     * @param tableName 表名称
     */
    @Override
    public void synchDb(String tableName)
    {
    	try {
    		GenTable table = genTableMapper.selectGenTableByName(tableName);
            List<GenTableColumn> tableColumns = table.getColumns();
            List<String> tableColumnNames = tableColumns.stream().map(GenTableColumn::getColumnName).collect(Collectors.toList());

            SysDataSource dataSource = dataSourceMapper.selectSysDataSource(table.getDataSourceId());
            List<GenTableColumn> dbTableColumns = selectDbTableColumnsByName(dataSource.getId(),tableName);
            List<String> dbTableColumnNames = dbTableColumns.stream().map(GenTableColumn::getColumnName).collect(Collectors.toList());

            dbTableColumns.forEach(column -> {
                if (!tableColumnNames.contains(column.getColumnName()))
                {
                    GenUtils.initColumnField(column, table);
                    genTableColumnMapper.insertGenTableColumn(column);
                }
            });

            List<GenTableColumn> delColumns = tableColumns.stream()
                    .filter(column -> !dbTableColumnNames.contains(column.getColumnName())).collect(Collectors.toList());
            if (StringUtils.isNotEmpty(delColumns))
            {
                genTableColumnMapper.deleteGenTableColumns(delColumns);
            }
		} catch (Exception e) {
			// 发生异常手动回滚
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
    }
    
    /**
     * 批量生成代码
     * 
     * @param tableNames 表数组
     * @return 数据
     */
    @Override
    public byte[] downloadCode(String[] tableNames)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames)
        {
            generatorCode(tableName, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 查询表信息并生成代码
     */
    private void generatorCode(String tableName, ZipOutputStream zip)
    {
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try
            {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table)));
                IOUtils.write(sw.toString(), zip, Constants.UTF8);
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
            }
            catch (IOException e)
            {
                log.error("渲染模板失败，表名：" + table.getTableName(), e);
            }
        }
    }

    /**
     * 修改保存参数校验
     * 
     * @param genTable 业务信息
     */
    @Override
    public void validateEdit(GenTable genTable)
    {
        if (GenConstants.TPL_TREE.equals(genTable.getTplCategory()))
        {
            String options = JSON.toJSONString(genTable.getParams());
            JSONObject paramsObj = JSONObject.parseObject(options);
            if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_CODE)))
            {
                throw new BusinessException("树编码字段不能为空");
            }
            else if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_PARENT_CODE)))
            {
                throw new BusinessException("树父编码字段不能为空");
            }
            else if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_NAME)))
            {
                throw new BusinessException("树名称字段不能为空");
            }
        }
        else if (GenConstants.TPL_SUB.equals(genTable.getTplCategory()))
        {
            if (StringUtils.isEmpty(genTable.getSubTableName()))
            {
                throw new BusinessException("关联子表的表名不能为空");
            }
            else if (StringUtils.isEmpty(genTable.getSubTableFkName()))
            {
                throw new BusinessException("子表关联的外键名不能为空");
            }
        }
    }

    /**
     * 设置主键列信息
     * 
     * @param table 业务表信息
     */
    public void setPkColumn(GenTable table)
    {
        for (GenTableColumn column : table.getColumns())
        {
            if (column.isPk())
            {
                table.setPkColumn(column);
                break;
            }
        }
        if (StringUtils.isNull(table.getPkColumn()))
        {
            table.setPkColumn(table.getColumns().get(0));
        }
        if (GenConstants.TPL_SUB.equals(table.getTplCategory()))
        {
            for (GenTableColumn column : table.getSubTable().getColumns())
            {
                if (column.isPk())
                {
                    table.getSubTable().setPkColumn(column);
                    break;
                }
            }
            if (StringUtils.isNull(table.getSubTable().getPkColumn()))
            {
                table.getSubTable().setPkColumn(table.getSubTable().getColumns().get(0));
            }
        }
    }

    /**
     * 设置主子表信息
     * 
     * @param table 业务表信息
     */
    public void setSubTable(GenTable table)
    {
        String subTableName = table.getSubTableName();
        if (StringUtils.isNotEmpty(subTableName))
        {
            table.setSubTable(genTableMapper.selectGenTableByName(subTableName));
        }
    }

    /**
     * 设置代码生成其他选项值
     * 
     * @param genTable 设置后的生成对象
     */
    public void setTableFromOptions(GenTable genTable)
    {
        JSONObject paramsObj = JSONObject.parseObject(genTable.getOptions());
        if (StringUtils.isNotNull(paramsObj))
        {
            String treeCode = paramsObj.getString(GenConstants.TREE_CODE);
            String treeParentCode = paramsObj.getString(GenConstants.TREE_PARENT_CODE);
            String treeName = paramsObj.getString(GenConstants.TREE_NAME);
            String parentMenuId = paramsObj.getString(GenConstants.PARENT_MENU_ID);
            String parentMenuName = paramsObj.getString(GenConstants.PARENT_MENU_NAME);
            
            genTable.setTreeCode(treeCode);
            genTable.setTreeParentCode(treeParentCode);
            genTable.setTreeName(treeName);
            genTable.setParentMenuId(parentMenuId);
            genTable.setParentMenuName(parentMenuName);
        }
    }

    /**
     * 获取代码生成地址
     * 
     * @param table 业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTable table, String template)
    {
        String genPath = table.getGenPath();
        if (StringUtils.equals(genPath, "/"))
        {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table);
    }
}