package com.cummins.cdc.flink.configuration;

import com.cummins.cdc.flink.sink.CustomSinkFunction;
import com.cummins.cdc.flink.FlinkStarter;
import com.cummins.cdc.flink.sink.FlinkConsumerListener;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class FlinkConf {

    @Bean(name = "flinkProperty")
    @ConfigurationProperties("flink")
    @ConditionalOnMissingBean
    public FlinkProperty flinkProperty() {
        return new FlinkProperty();
    }

    @Bean
    @ConditionalOnMissingBean
    public SinkFunction sinkFunction(List<FlinkConsumerListener> flinkConsumerListenerList) {
        return new CustomSinkFunction(flinkConsumerListenerList,4725);
    }

    @Bean
    @ConditionalOnMissingBean
    public FlinkStarter flinkStarter(FlinkProperty flinkProperty, SinkFunction sinkFunction) {
        FlinkStarter flinkStarter = new FlinkStarter(flinkProperty, sinkFunction);
        flinkStarter.init();
        return flinkStarter;
    }

}
