package org.example.address_analysis.db.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.address_analysis.db.entity.AddressFilter;
import org.example.address_analysis.db.mapper.AddressFilterMapper;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * 地址过滤表(address_filter)数据DAO
 *
 * @author kancy
 * @since 2024-03-07 16:58:28
 * @description 由 Mybatisplus Code Generator 创建
 */
@Slf4j
@Repository
public class AddressFilterDao extends ServiceImpl<AddressFilterMapper, AddressFilter> {

    public Set<String> listAddress() {
        return baseMapper.listAddress();
    }
}
