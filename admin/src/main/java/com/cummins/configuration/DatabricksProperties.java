package com.cummins.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by newforesee on 2022/8/24
 */
@Component
@ConfigurationProperties(prefix = "databricks")
public class DatabricksProperties {
    private String driverClassName;
    private String url;
    private String user;
    private String if_exists;
    private Integer initialSize;
    private Integer maxActive;
    private Integer maxIdle;
    private Integer minIdle;
    private Integer maxWait;
    private Boolean defaultAutoCommit;

    public String getDriverClassName() {
        return driverClassName;
    }

    public DatabricksProperties setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DatabricksProperties setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUser() {
        return user;
    }

    public DatabricksProperties setUser(String user) {
        this.user = user;
        return this;
    }

    public String getIf_exists() {
        return if_exists;
    }

    public DatabricksProperties setIf_exists(String if_exists) {
        this.if_exists = if_exists;
        return this;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public DatabricksProperties setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
        return this;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public DatabricksProperties setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
        return this;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public DatabricksProperties setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public DatabricksProperties setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
        return this;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public DatabricksProperties setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
        return this;
    }

    public Boolean getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public DatabricksProperties setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
        return this;
    }

}
