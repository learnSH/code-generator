# code-generator

#### 介绍
代码生成工具
抽取自“若依管理系统”-代码生成模块，可在本地运行，无需依赖服务器

传送门：https://gitee.com/y_project/RuoYi

#### 项目说明
1. 代码参考若依管理系统
2. 数据库采用sqlite，位置在src\main\resources\static\sqlite\code-generator.db，发布后请将db文件放在jar包的同级sqlite目录中
3. 目前目标数据库只支持MySQL、Oracle、PostgreSQL、SQLServer其他的可自行扩展
4. 数据源可多条配置，导入表结构时进行选择数据源
5. 工具目录中tools文件是jar文件，只是改了个名字，双击.bat启动

#### 工具下载
https://gitee.com/lpf_project/code-generator/releases/v1.0

#### 测试说明

1. 双击“启动工具.bat”进行打开
2. 工具内置tomcat，默认端口为：5064
3. 如果需要更改端口号，打开“启动工具.bat”找到“--server.port=5064”更改
4. tomcat启动后会自动打开浏览器，优先级为：谷歌→火狐→-系统默认浏览器；如果无法自动打开，可以手动打开浏览器输入：http://127.0.0.1:5064