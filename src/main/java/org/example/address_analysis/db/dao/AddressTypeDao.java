package org.example.address_analysis.db.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.address_analysis.db.entity.AddressGroup;
import org.example.address_analysis.db.entity.AddressType;
import org.example.address_analysis.db.mapper.AddressGroupMapper;
import org.example.address_analysis.db.mapper.AddressTypeMapper;
import org.springframework.stereotype.Repository;

/**
 * 地址组表(address_group)数据DAO
 *
 * @author kancy
 * @since 2024-03-07 16:58:28
 * @description 由 Mybatisplus Code Generator 创建
 */
@Slf4j
@Repository
public class AddressTypeDao extends ServiceImpl<AddressTypeMapper, AddressType> {
}
