package org.example.address_analysis.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.address_analysis.db.entity.FmisAssetGroup;

import java.util.Set;

@Mapper
public interface FmisAssetGroupMapper extends BaseMapper<FmisAssetGroup> {
    Set<Integer> listExchangeGroupId();

    Set<String> listExchangeCollectAddress(@Param("groupId") Integer groupId);
}
