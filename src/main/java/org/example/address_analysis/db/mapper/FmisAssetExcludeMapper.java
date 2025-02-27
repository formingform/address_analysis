package org.example.address_analysis.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.address_analysis.db.entity.FmisAssetExclude;

import java.util.Set;

@Mapper
public interface FmisAssetExcludeMapper extends BaseMapper<FmisAssetExclude> {
    Set<String> listAllExcluded();

    Set<String> listExchangeCollectAddress();
}
