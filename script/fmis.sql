DROP TABLE IF EXISTS `fmis_asset_address`;

CREATE TABLE `fmis_asset_address` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `group_id` int(11) NOT NULL COMMENT '所属资产对象ID',
    `address` varchar(42) NOT NULL COMMENT '资产地址',
    `type` int(1) NOT NULL COMMENT '地址类型：0-初始地址，1-关联地址',
    `tag` varchar(50) DEFAULT NULL COMMENT '标签',
    `balance` decimal(64,0) NOT NULL DEFAULT '0' COMMENT '地址余额',
    `balance_block` bigint(20) NOT NULL DEFAULT '0' COMMENT '当前余额对应的区块',
    `balance_block_day` date not null default '1970-01-01',
    `rec_block` bigint(20) NOT NULL default 0 COMMENT '此地址被识别出来时的区块号',
    `rec_block_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '此地址被识别出来时的区块时间',
    `proportion` bigint NULL COMMENT '流通占比',
    `remark` varchar(50) DEFAULT NULL COMMENT '备注',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `address` (`address`),
    KEY `rec_block` (`rec_block`)
) ;



DROP TABLE IF EXISTS `fmis_asset_group`;

CREATE TABLE `fmis_asset_group` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(50) NOT NULL COMMENT '资产对象名称',
    `type` int(1) NOT NULL COMMENT '资产对象类型：0-大户, 1-交易所',
    `rec_block` bigint(20) NOT NULL DEFAULT '-1' COMMENT '识别时的块号（手动添加的默认为-1）',
    `update_block` bigint not null default 0,
    `balance` decimal(64,0) NOT NULL DEFAULT '0' COMMENT '总资产余额，由定时任务从地址表汇总而来',
    `proportion` decimal(21,18) NOT NULL DEFAULT '0.000000000000000000' COMMENT '流通量占比',
    `address_count` int(11) NOT NULL DEFAULT '0' COMMENT '地址数',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`),
    KEY `balance` (`balance`)
);


DROP TABLE IF EXISTS `fmis_asset_tx`;

CREATE TABLE `fmis_asset_tx` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `tfrom` varchar(42) NOT NULL COMMENT '交易from地址',
    `tfrom_aid` int(11) DEFAULT '-1' COMMENT 'from所属资地址ID',
    `tfrom_gid` int(11) DEFAULT '-1' COMMENT 'from所属资产组ID',
    `tto` varchar(42) NOT NULL COMMENT '交易to地址',
    `tto_aid` int(11) DEFAULT '-1' COMMENT 'to所属资产地址ID',
    `tto_gid` int(11) DEFAULT '-1' COMMENT 'to所属资产组ID',
    `hash` varchar(128) NOT NULL COMMENT '交易Hash',
    `amount` decimal(64,0) NOT NULL DEFAULT '0' COMMENT '交易金额',
    `block_number` bigint(20) NOT NULL DEFAULT '0' COMMENT '交易所属区块号',
    `block_day` date not null default '1970-01-01' COMMENT '区块日期',
    `block_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `hash` (`hash`),
    KEY `tfrom` (`tfrom`),
    KEY `tto` (`tto`),
    KEY `block_day` (`block_day`),
    KEY `tfrom_gid` (`tfrom_gid`),
    KEY `tto_gid` (`tto_gid`),
    KEY `tfrom_2` (`tfrom`,`tto`),
    KEY `tfrom_gid_2` (`tfrom_gid`,`tto_gid`),
    KEY `tto_aid` (`tto_aid`),
    KEY `tfrom_aid` (`tfrom_aid`)
);
