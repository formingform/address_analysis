package org.example.address_analysis.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.address_analysis.db.entity.TransferItem;
import org.example.address_analysis.db.mapper.TransferItemMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 转移记录(transfer_item)数据DAO
 *
 * @author kancy
 * @description 由 Mybatisplus Code Generator 创建
 * @since 2024-03-08 10:03:47
 */
@Slf4j
@Repository
public class TransferItemDao extends ServiceImpl<TransferItemMapper, TransferItem> {

    public List<TransferItem> listByRange(long curId, Integer limit) {
        LambdaQueryWrapper<TransferItem> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderBy(true, true, TransferItem::getId);

        queryWrapper.gt(TransferItem::getId, curId);
        queryWrapper.last("limit " + limit);
        return list(queryWrapper);
    }
}
