package org.example.address_analysis.db.typehandler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.example.address_analysis.db.entity.RelatedAddress;

public class RelatedAddressTypeHandler  extends AbstractJsonTypeHandler<Object> {
    @Override
    protected Object parse(String json) {
        return JSONArray.parseArray(json, RelatedAddress.class);
    }

    @Override
    protected String toJson(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.LargeObject);
    }
}
