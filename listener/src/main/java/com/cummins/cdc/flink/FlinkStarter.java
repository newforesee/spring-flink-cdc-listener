package com.cummins.cdc.flink;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.cummins.cdc.flink.configuration.FlinkProperty;
import com.cummins.cdc.flink.util.JsonDebeziumDeserializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
public class FlinkStarter {

    private FlinkProperty flinkProperty;

    private SinkFunction sinkFunction;

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public FlinkStarter(FlinkProperty flinkProperty, SinkFunction sinkFunction) {
        this.flinkProperty = flinkProperty;
        this.sinkFunction = sinkFunction;
    }

    public void init() {
        executorService.submit(() -> {
            try {
                start();
            } catch (Exception e) {
                log.error("flink cdc start error", e);
            }
        });

    }

    //    @PostConstruct
    public void start() throws Exception {

        StreamExecutionEnvironment bsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings bsSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(bsEnv, bsSettings);

        //todo:从配置文件获取并行度
        bsEnv.setParallelism(flinkProperty.getParallelism());

        bsEnv.enableCheckpointing(5000L, CheckpointingMode.EXACTLY_ONCE);
        bsEnv.getCheckpointConfig().setPreferCheckpointForRecovery(true);


        //设置job名称
        tEnv.getConfig().getConfiguration().setString("pipeline.name", flinkProperty.getPipelineName());

        bsEnv.setRestartStrategy(RestartStrategies.failureRateRestart(
                3, // max failures per unit
                Time.of(5, TimeUnit.MINUTES), //time interval for measuring failure rate
                Time.of(10, TimeUnit.SECONDS) // delay

        ));

        if (CollectionUtils.isEmpty(flinkProperty.getMysqlDataSource())) {
            return;
        }
        for (FlinkProperty.MysqlSourceProperty mysqlSourceProperty : flinkProperty.getMysqlDataSource()) {
            SourceFunction<JSONObject> sourceFunction = generateMysqlSource(mysqlSourceProperty);
            DataStreamSource dataStreamSource = bsEnv.addSource(sourceFunction);
            dataStreamSource.addSink(sinkFunction);
        }
        bsEnv.execute();
    }

/*    public void main(String[] args) throws Exception {

        SourceFunction<JSONObject> sourceFunction = generateMysqlSource(flinkProperty.getMysqlDataSource().get(0));

        // 创建 Flink 执行环境。
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.setRestartStrategy(RestartStrategies.failureRateRestart(
            3, // max failures per unit
            Time.of(5, TimeUnit.MINUTES), //time interval for measuring failure rate
            Time.of(10, TimeUnit.SECONDS) // delay

        ));
        // 通过 flink-cdc 创建 sourceFunction。
        // MySqlSource<String> sourceFunction = MySQLCrsSource.create();

        env.addSource(sourceFunction)
            .addSink(sinkFunction).name("CRS RecordStringSink Sink");

        env.execute("Listener MySQL CRS Snapshot + Binlog");
    }*/

    private SourceFunction<JSONObject> generateMysqlSource(FlinkProperty.MysqlSourceProperty sourceProperty) {

        MySQLSource.Builder<JSONObject> mysqlSourceBuilder = MySQLSource.<JSONObject>builder();
        if (sourceProperty.getServerId() != null) {
            mysqlSourceBuilder.serverId(sourceProperty.getServerId());
        }

        mysqlSourceBuilder.hostname(sourceProperty.getHostname())
                .port(sourceProperty.getPort())
                .databaseList(sourceProperty.getDatabaseList())
                .username(sourceProperty.getUsername())
                .password(sourceProperty.getPassword())
                .tableList(sourceProperty.getTableList())
                .deserializer(new JsonDebeziumDeserializationSchema());
        return mysqlSourceBuilder.build();
    }


}
