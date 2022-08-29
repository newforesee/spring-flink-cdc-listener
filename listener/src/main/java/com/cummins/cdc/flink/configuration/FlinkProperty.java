package com.cummins.cdc.flink.configuration;

import lombok.Data;

import java.util.List;


@Data
public class FlinkProperty {

    private String pipelineName = "flinkCDC";

    /**
     * mysql数据源
     */
    private List<MysqlSourceProperty> mysqlDataSource;


    @Data
    public static class MysqlSourceProperty {
        /**
         * 端口
         */
        private int port = 3306;

        /**
         * 主机
         */
        private String hostname;

        /**
         * 监听的数据库列表
         */
        private String[] databaseList;

        /**
         * 用户
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 数据库的serverId
         */
        private Integer serverId;

        /**
         * 时区
         */
        private String serverTimeZone;

        /**
         * 监听的表
         */
        private String[] tableList;
    }
}
