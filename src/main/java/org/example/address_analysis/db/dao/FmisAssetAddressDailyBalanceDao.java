package org.example.address_analysis.db.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.address_analysis.db.entity.FmisAssetAddressDailyBalance;
import org.example.address_analysis.db.mapper.FmisAssetAddressDailyBalanceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FmisAssetAddressDailyBalanceDao extends ServiceImpl<FmisAssetAddressDailyBalanceMapper, FmisAssetAddressDailyBalance>  {
    public void saveOrUpdateOnDuplicatedBatch(List<FmisAssetAddressDailyBalance> dailyBalanceAssetList) {
        baseMapper.saveOrUpdateOnDuplicatedBatch(dailyBalanceAssetList);
    }
}
