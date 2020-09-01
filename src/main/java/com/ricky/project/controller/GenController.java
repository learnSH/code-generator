package com.ricky.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ricky.common.utils.StringUtils;
import com.ricky.common.utils.text.Convert;
import com.ricky.framework.web.controller.BaseController;
import com.ricky.framework.web.domain.AjaxResult;
import com.ricky.framework.web.domain.CxSelect;
import com.ricky.framework.web.page.TableDataInfo;
import com.ricky.project.domain.GenTable;
import com.ricky.project.domain.GenTableColumn;
import com.ricky.project.domain.SysDataSource;
import com.ricky.project.service.IGenTableColumnService;
import com.ricky.project.service.IGenTableService;
import com.ricky.project.service.ISysDataSourceService;

/**
 * 代码生成 操作处理
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/tool/gen")
public class GenController extends BaseController
{
    private String prefix = "tool/gen";

    @Autowired
    private IGenTableService genTableService;

    @Autowired
    private IGenTableColumnService genTableColumnService;

    @Autowired
    private ISysDataSourceService sysDataSourceService;
    
    @GetMapping()
    public String gen()
    {
        return prefix + "/gen";
    }

    /**
     * 查询代码生成列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo genList(GenTable genTable)
    {
        startPage();
        List<GenTable> list = genTableService.selectGenTableList(genTable);
        return getDataTable(list);
    }

    /**
     * 查询数据库列表
     */
    @PostMapping("/db/list")
    @ResponseBody
    public TableDataInfo dataList(GenTable genTable)
    {
    	SysDataSource dataSource = sysDataSourceService.selectSysDataSource();
        startPage();
        List<GenTable> list = genTableService.selectDbTableList(genTable,dataSource.getDriver());
        return getDataTable(list);
    }

    /**
     * 查询数据表字段列表
     */
    @PostMapping("/column/list")
    @ResponseBody
    public TableDataInfo columnList(GenTableColumn genTableColumn)
    {
        TableDataInfo dataInfo = new TableDataInfo();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(genTableColumn);
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }
    
    /**
     * 导入表结构
     */
    @GetMapping("/importTable")
    public String importTable()
    {
    	SysDataSource dataSource = sysDataSourceService.selectSysDataSource();
    	if (dataSource == null) {
    		return prefix + "/dataSourceError";
		}
        return prefix + "/importTable";
    }

    /**
     * 导入表结构（保存）
     */
    @PostMapping("/importTable")
    @ResponseBody
    public AjaxResult importTableSave(String tables)
    {
        String[] tableNames = Convert.toStrArray(tables);
        SysDataSource dataSource = sysDataSourceService.selectSysDataSource();
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames,dataSource.getDriver());
        genTableService.importGenTable(tableList);
        return AjaxResult.success();
    }

    /**
     * 修改代码生成业务
     */
    @GetMapping("/edit/{tableId}")
    public String edit(@PathVariable("tableId") Long tableId, ModelMap mmap)
    {
    	 GenTable table = genTableService.selectGenTableById(tableId);
         List<GenTable> genTables = genTableService.selectGenTableAll();
         List<CxSelect> cxSelect = new ArrayList<CxSelect>();
         for (GenTable genTable : genTables)
         {
             if (!StringUtils.equals(table.getTableName(), genTable.getTableName()))
             {
                 CxSelect cxTable = new CxSelect(genTable.getTableName(), genTable.getTableName() + '：' + genTable.getTableComment());
                 List<CxSelect> cxColumns = new ArrayList<CxSelect>();
                 for (GenTableColumn tableColumn : genTable.getColumns())
                 {
                     cxColumns.add(new CxSelect(tableColumn.getColumnName(), tableColumn.getColumnName() + '：' + tableColumn.getColumnComment()));
                 }
                 cxTable.setS(cxColumns);
                 cxSelect.add(cxTable);
             }
         }
         mmap.put("table", table);
         mmap.put("data", JSON.toJSON(cxSelect));
        return prefix + "/edit";
    }

    /**
     * 修改保存代码生成业务
     */
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated GenTable genTable)
    {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
        return AjaxResult.success();
    }

    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        genTableService.deleteGenTableByIds(ids);
        return AjaxResult.success();
    }

    /**
     * 预览代码
     */
    @GetMapping("/preview/{tableId}")
    @ResponseBody
    public AjaxResult preview(@PathVariable("tableId") Long tableId) throws IOException
    {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return AjaxResult.success(dataMap);
    }

    /**
     * 生成代码（下载方式）
     */
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException
    {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }
    
    /**
     * 生成代码（自定义路径）
     */
    @GetMapping("/genCode/{tableName}")
    @ResponseBody
    public AjaxResult genCode(HttpServletResponse response, @PathVariable("tableName") String tableName)
    {
        genTableService.generatorCode(tableName);
        return AjaxResult.success();
    }

    /**
     * 批量生成代码
     */
    @GetMapping("/batchGenCode")
    @ResponseBody
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException
    {
        String[] tableNames = Convert.toStrArray(tables);
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException
    {
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}