package com.cummins.cdc.flink.sink;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface FlinkConsumerListener<T> extends Serializable {

    String getDBName();

    String getTable();

    void insert(T data) throws SQLException;

    void batch_insert(List<T> dataList) throws SQLException;

    void update(T srcData, T destData);

    void delete(T data);
}
