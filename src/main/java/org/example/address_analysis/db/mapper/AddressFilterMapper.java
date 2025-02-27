package org.example.address_analysis.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.address_analysis.db.entity.AddressFilter;

import java.util.Set;

/**
 * 地址过滤表(address_filter)数据Mapper
 *
 * @author kancy
 * @since 2024-03-07 16:58:28
 * @description 由 Mybatisplus Code Generator 创建
*/
@Mapper
public interface AddressFilterMapper extends BaseMapper<AddressFilter> {

    Set<String> listAddress();
}
