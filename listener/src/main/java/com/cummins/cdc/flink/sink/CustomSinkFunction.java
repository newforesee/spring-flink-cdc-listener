package com.cummins.cdc.flink.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomSinkFunction implements SinkFunction<JSONObject> {

    //todo:实现批量导入
    //


    Boolean BATCH_MODE;
    Integer batchSize;

    List<FlinkConsumerListener> consumerListenerList;
    Map<String, FlinkConsumerListener> consumerListenerMap = new HashMap<>();

    List<Object> batchInsertList = new ArrayList<>();
    Integer batches;


    /**
     * 泛型
     */
    Map<String, Class> consumerGenericTMap = new HashMap<>();

    public CustomSinkFunction(List<FlinkConsumerListener> consumerListenerList, Integer batchSize) {
        this.consumerListenerList = consumerListenerList;
        //todo:从配置文件中获取并行度
        this.batchSize = batchSize / 4;
        init();
    }

    private void init() {

        BATCH_MODE = true;

        for (FlinkConsumerListener consumerListener : consumerListenerList) {
            consumerListenerMap.put(consumerListener.getDBName() + "." + consumerListener.getTable(), consumerListener);
            Class clazz = getInterfaceT(consumerListener.getClass());
            consumerGenericTMap.put(consumerListener.getDBName() + "." + consumerListener.getTable(), clazz);
        }

        if (batchSize % 1000 == 0) {
            batches = batchSize / 1000;
        } else {
            batches = (batchSize / 1000) + 1;
        }
        log.warn("batches:{} ; batchSize:{}", batches, batchSize);


    }

    @Override
    public void invoke(JSONObject value, Context context) throws Exception {

        JSONObject source = (JSONObject) value.get("source");
        JSONObject before = null;
        JSONObject after = null;

        if (value.get("before") != null) {
            before = (JSONObject) value.get("before");
        }
        if (value.get("after") != null) {
            after = (JSONObject) value.get("after");
        }

        String db = source.get("db")
                .toString();
        String table = source.get("table")
                .toString();
        FlinkConsumerListener consumerListener = consumerListenerMap.get(db + "." + table);


        if (consumerListener == null) {
            log.warn("{} 消费者不存在", db + "." + table);
            return;
        }
        Class clazz = consumerGenericTMap.get(db + "." + table);
        if (clazz == null) {
            log.warn("{} 泛型检查失败！", db + "." + table);
            return;
        }

        if (BATCH_MODE) {

            int executionThreshold;
            if (batches > 1) {
                executionThreshold = 1000;
            } else {
                executionThreshold = batchSize % 1000;
            }

            Object afterObj = JSON.parseObject(after.toJSONString(), clazz);
            batchInsertList.add(afterObj);
            //log.warn("executionThreshold:{} ; batchInsertList.size:{}",executionThreshold,batchInsertList.size());


            if (batchInsertList.size() == executionThreshold) {
                consumerListener.batch_insert(batchInsertList);
                batchInsertList.clear();
                batches = batches - 1;
            }
            if (batches == 0) {
                BATCH_MODE = false;
            }


        } else {

            if (before != null && after != null) {
                Object beforeObj = JSON.parseObject(before.toJSONString(), clazz);
                Object afterObj = JSON.parseObject(after.toJSONString(), clazz);
                consumerListener.update(beforeObj, afterObj);
                return;
            }

            if (before != null) {
                Object beforeObj = JSON.parseObject(before.toJSONString(), clazz);
                consumerListener.delete(beforeObj);
                return;
            }

            if (after != null) {
                Object afterObj = JSON.parseObject(after.toJSONString(), clazz);
                consumerListener.insert(afterObj);
                return;
            }
        }

    }

    /**
     * 获取泛型的class
     *
     * @param clzz
     * @return
     */
    public Class getInterfaceT(Class clzz) {
        Type[] types = clzz.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Type claz = pType.getActualTypeArguments()[0];
                if (claz instanceof Class) {
                    return (Class) claz;
                }
            }
        }
        return null;
    }
}
