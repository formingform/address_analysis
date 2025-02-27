package org.example.address_analysis;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.address_analysis.db.dao.TransferItemDao;
import org.example.address_analysis.db.entity.TransferItem;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Slf4j
@EnableTransactionManagement
@MapperScan(basePackages = {"org.example.address_analysis.db.mapper"})
@SpringBootApplication
public class AddressAnalysisApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AddressAnalysisApplication.class, args);
    }
    @Resource
    private AddressAnalysisService addressAnalysisService;

    @Resource
    private TransferItemDao transferItemDao;
    @Resource
    private AddressAnalysisConfig addressAnalysisConfig;


    @Override
    public void run(String... args) {

        // 初始化服务
        if ( args!=null && args.length == 1 && args[0].equalsIgnoreCase("exchange")) {
            addressAnalysisService.initExchange();
            log.info(">>>>开始分析交易所的充币地址");
            this.exchange();
            log.info("结束分析交易所的关联地址<<<<");

        }


        if ( args!=null && args.length == 1 && args[0].equalsIgnoreCase("major")) {
            addressAnalysisService.initMajor();
            log.info(">>>>开始分析大户的关联地址");
            this.major();
            log.info("结束分析大户的关联地址<<<<");
        }



        //从交易所识别流程识别出的所有地址都清除，他们不应是大户的初始地址
        //如果一次完成交易所/大户分析时需要
        /*if ( args == null || args.length == 0 ){

            addressAnalysisService.repairMajor();
        }*/

        if (args == null || args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("balance"))){
            log.info(">>>>开始分批更新大户/交易所资产地址的余额");
            addressAnalysisService.fetchBalance();
            log.info("完成更新大户/交易所资产地址的余额<<<<");
        }


        System.exit(0);

    }


    private void exchange(){
        long curId = addressAnalysisConfig.getStartId();
        // 查询交易记录
        log.debug("从转账交易中，分析交易所的充币地址");
        List<TransferItem> transferItemList = transferItemDao.listByRange(curId, addressAnalysisConfig.getLimit());
        int batch = 0;
        while (!transferItemList.isEmpty()) {
            log.debug("批量:{}", ++batch);
            for (TransferItem transferItem : transferItemList) {
                addressAnalysisService.processExchange(transferItem);
            }
            curId = transferItemList.get(transferItemList.size() - 1).getId();
            transferItemList = transferItemDao.listByRange(curId, addressAnalysisConfig.getLimit());
        }
        log.debug("保存交易所的充币地址");
        addressAnalysisService.saveExchange();
    }


    private void major(){
        long curId = addressAnalysisConfig.getStartId();
        // 查询交易记录
        log.debug("从转账交易中，分析大户的关联地址");
        int batch = 0;
        List<TransferItem> transferItemList = transferItemDao.listByRange(curId, addressAnalysisConfig.getLimit());
        while (!transferItemList.isEmpty()) {
            log.debug("批量:{}", ++batch);
            for (TransferItem transferItem : transferItemList) {
                addressAnalysisService.processMajor(transferItem);
            }
            curId = transferItemList.get(transferItemList.size() - 1).getId();
            transferItemList = transferItemDao.listByRange(curId, addressAnalysisConfig.getLimit());
        }
        log.debug("保存大户的关联地址");
        addressAnalysisService.updateAddressGroup();
    }
}
