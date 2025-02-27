package org.example.address_analysis.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.address_analysis.db.entity.FmisAssetAddress;
import org.example.address_analysis.db.mapper.FmisAssetAddressMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Slf4j
@Repository
public class FmisAssetAddressDao extends ServiceImpl<FmisAssetAddressMapper, FmisAssetAddress> {
    public List<FmisAssetAddress> listAssetAddressByRange(long curId, Integer limit) {
        LambdaQueryWrapper<FmisAssetAddress> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderBy(true, true, FmisAssetAddress::getId);

        queryWrapper.gt(FmisAssetAddress::getId, curId);
        queryWrapper.last("limit " + limit);

        return  list(queryWrapper);

    }

    public void updateBalanceBatch(List<FmisAssetAddress> updateAssetAddressBalanceList) {
        baseMapper.updateBalanceBatch(updateAssetAddressBalanceList);
    }
}
