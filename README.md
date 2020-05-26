# dataSync
dataSync是一个将一个数据库的结构和数据同步到另外一个数据库的工具，并使用quartz实现了定时同步功能。

使用教程：
将dataSync.jar包和我们自定义的db.xml放在同一个文件夹下，执行java -jar dataSync.jar即可。

db.xml文件参考如下
<?xml version="1.0" encoding="UTF-8"?>
<dbs>
    <!-- 源数据库 -->
    <from.db id="origin">
        <url>ip:port/dbname</url>
        <username>**</username>
        <password>**</password>
        <db.type>mysql</db.type>
    </from.db>
    <!-- 目标数据库 -->
    <dest.db id="target">
        <url>ip:port/dbname</url>
        <username>**</username>
        <password>**</password>
        <db.type>mysql</db.type>
    </dest.db>
    <!-- 同步定时器 -->
    <corn>0 0/2 * * * ?</corn>
    <data.sync>
        <!-- 是否开启数据同步 请勿用于大批量数据同步 -->
        <flag>true</flag>
        <!-- 每次同步数据条数 -->
        <limit>3000</limit>
    </data.sync>
</dbs>
