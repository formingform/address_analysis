package org.example.address_analysis.db.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.address_analysis.db.entity.FmisAssetExclude;
import org.example.address_analysis.db.mapper.FmisAssetExcludeMapper;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Slf4j
@Repository
public class FmisAssetExcludeDao extends ServiceImpl<FmisAssetExcludeMapper, FmisAssetExclude> {
    // 大户分析需要排除的地址：
    // 1. fmis_asset_execude表中的地址
    // 2. fmis_asset_address中，交易所的地址（包括初始地址，关联地址）
    public Set<String> listAllExcluded() {
        return baseMapper.listAllExcluded();
    }

    public Set<String> listExchangeCollectAddress() {
        return baseMapper.listExchangeCollectAddress();
    }
}
