package com.cummins.cdc.flink.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ververica.cdc.debezium.DebeziumDeserializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;
import java.util.Map;


@Slf4j
public class JsonDebeziumDeserializationSchema implements DebeziumDeserializationSchema<JSONObject> {
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<JSONObject> collector) {
        JSONObject resJson = new JSONObject();
        try {

            Struct valueStruct = (Struct) sourceRecord.value();
            Struct sourceStruct = valueStruct.getStruct("source");
            Struct afterStruct = valueStruct.getStruct("after");
            Struct beforeStruct = valueStruct.getStruct("before");
            resJson.put("source", toJson(sourceStruct));
            if (afterStruct != null) {
                resJson.put("after", toJson(afterStruct));
            }
            if (beforeStruct != null) {
                resJson.put("before", toJson(beforeStruct));
            }
        } catch (Exception e) {
            log.error("Deserialize throws exception:", e);
        }
        collector.collect(resJson);
    }

    @Override
    public TypeInformation<JSONObject> getProducedType() {
        return BasicTypeInfo.of(JSONObject.class);
    }

    private Map<String, Object> toJson(Struct struct) {
        JSONObject resJson = new JSONObject();
        List<Field> fields = struct.schema().fields();
        String name;
        Object value;
        for (Field field : fields) {
            name = field.name();
            value = struct.get(name);
            resJson.put(name, value);
        }
        return resJson;
    }

}
