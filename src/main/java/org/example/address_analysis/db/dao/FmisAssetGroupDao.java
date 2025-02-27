package org.example.address_analysis.db.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.address_analysis.db.entity.FmisAssetGroup;
import org.example.address_analysis.db.mapper.FmisAssetGroupMapper;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Slf4j
@Repository
public class FmisAssetGroupDao  extends ServiceImpl<FmisAssetGroupMapper, FmisAssetGroup> {
    public Set<Integer> listExchangeGroupId() {
        return baseMapper.listExchangeGroupId();
    }

    public Set<String> listExchangeCollectAddress(Integer groupId) {
        return baseMapper.listExchangeCollectAddress(groupId);
    }
}

