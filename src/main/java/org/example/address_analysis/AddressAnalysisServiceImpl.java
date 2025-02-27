package org.example.address_analysis;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.address_analysis.db.dao.*;
import org.example.address_analysis.db.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddressAnalysisServiceImpl implements AddressAnalysisService {
    @Resource
    private AddressGroupDao addressGroupDao;
    @Resource
    private AddressGroupDataDao addressGroupDataDao;

    @Resource
    private AddressAnalysisConfig addressAnalysisConfig;
    @Resource
    private FmisAssetExcludeDao fmisAssetExcludeDao;

    @Resource
    private FmisAssetGroupDao fmisAssetGroupDao;

    @Resource
    private FmisAssetAddressDao fmisAssetAddressDao;

    @Resource
    private AddressTypeDao addressTypeDao;

    @Resource
    private FmisAssetAddressDailyBalanceDao fmisAssetAddressDailyBalanceDao;

    @Resource
    private CentralNodeApi centralNodeApi;

    private List<AddressGroup> majorList = new ArrayList<>();


    // 大户分析需要排除的地址：
    // 1. fmis_asset_execude表中的地址
    // 2. fmis_asset_address中，交易所的初始地址
    // 分析大户时， 这些地址的交易，不会增加这些地址所在大户的关联地址
    private Set<String> filterAddresses = new HashSet<>();

    private Map<String, String> majorAddressNameMap = new HashMap<>();

    private Map<String, AddressGroup> majorGroupMap = new HashMap<>();

    // 保存每个交易所的归集地址集合
    private Map<Integer, Set<String>> exchangeCollectAddressMap;

    private Map<Integer, Set<RelatedAddress>> exchangeRelatedAddressMap = new HashMap<>();
    // 每个交易所的归集地址对应的交易所group_id
    private Map<String, Integer> exchangeCollectAddressGroupIdMap;

    private Long contractCheckedBlock = 0L;
    private Map<String, Boolean> addressTypeMap = new HashMap();

    @Override
    public void initExchange() {
        exchangeCollectAddressMap = this.listExchangeCollectAddressMap();
        exchangeCollectAddressGroupIdMap = convertExchangeCollectAddressGroupIdMap(exchangeCollectAddressMap);

        /*log.info("初始化排除地址");
        Set<String> excludedAddresses = fmisAssetExcludeDao.listAllExcluded();
        Set<String> exchangeAddresses = fmisAssetExcludeDao.listExchangeAddress();*/

       /* filterAddresses.addAll(excludedAddresses);
        filterAddresses.addAll(exchangeAddresses);*/
        initMajor();
    }

    @Override
    public void initMajor() {
        log.info("初始化大户group");
        List<AddressGroupData> groupInitList = addressGroupDataDao.list();
        groupInitList.forEach(initData ->{
            AddressGroup group = majorGroupMap.get(initData.getGroupName());
            if(group==null){
                group = new AddressGroup();
                group.setGroupName(initData.getGroupName());
                majorGroupMap.put(group.getGroupName(), group);

                majorList.add(group);
            }
            group.addInitAddress(initData.getInitialAddress());
            group.addRelatedAddress(initData.getInitialAddress(), 0L, 0L);

            majorAddressNameMap.put(initData.getInitialAddress(), group.getGroupName());
        });

        // 大户分析需要排除的地址：
        // 1. fmis_asset_execude表中的地址
        // 2. fmis_asset_address中，交易所的初始地址
        // 分析大户时， 这些地址的交易，不会增加这些地址所在大户的关联地址
        log.info("初始化排除地址");
        Set<String> excludedAddresses = fmisAssetExcludeDao.listAllExcluded();
        Set<String> exchangeCollectAddresses = fmisAssetExcludeDao.listExchangeCollectAddress();

        filterAddresses.addAll(excludedAddresses);
        filterAddresses.addAll(exchangeCollectAddresses);

        //设置新的group_id的起始序号。
        // 不需要：静态分析，不会增加新的大户
        //AddressGroup.atomicInteger = new AtomicInteger(majorList.get(majorList.size() - 1).getId() + 1);

        log.info("初始化地址类型");
        List<AddressType> addressTypeList = addressTypeDao.list();
        addressTypeList.forEach(addressType -> {
            addressTypeMap.put(addressType.getAddress(), addressType.getContract());
        });
    }
    @Override
    public void processExchange(TransferItem transferItem) {
        String from = transferItem.getTxFrom();
        String to = transferItem.getTxTo();

        if (from.equalsIgnoreCase(to)) {
            return;
        }

        // 关于充币地址
        // 在逻辑上：
        // 充币地址，是作为大户的关联地址，而不是交易所的关联地址。
        // 在存储上：
        // 充币地址和大户的关系，存储于fmis_asset_address表
        // 充币地址和大户的关系，也存储于fmis_asset_address表，但是目前来说，没有实际作用，只是作为以后有统计交易所充币地址的需求时，有可能可以用上。
        // 识别交易所的充值地址：
        // 如果to是交易所归集地址，
        // from不是其它交易所的地址（包括初始地址，关联地址），也不是排除地址，则把from增加为to的充币地址
        // 统计大户的充币时：
        // from = 大户所有地址， to: 交易所归集地址
        // 统计大户的提币时：
        // from = 交易所归集地址， to: 大户所有地址

        // from = 大户所有地址, to = 其它大户所有地址（作为大户之间的流转）

        // 统计交易所的充币时：
        // from = 任何非交易所归集地址， to: 交易所归集地址
        // 统计交易所的提币时：
        // from = 交易所归集地址， to: 任何非交易所归集地址

        // from = 交易所归集地址, to = 其它交易所归集地址（作为交易所之间的流转）

        // 识别交易所的充币地址：
        // to = 归集地址, from = 非交易所归集地址 && 非排除地址
        if (exchangeCollectAddressGroupIdMap.containsKey(to) && !exchangeCollectAddressGroupIdMap.containsKey(from) && !filterAddresses.contains(from)) {
            this.addExchangeRelatedAddress(exchangeCollectAddressGroupIdMap.get(to), from, transferItem.getBlockNumber(), transferItem.getBlockTimestamp().getTime());
            // 把识别的交易所充币地址，加入过滤地址，下次此地址就不会再别识别为交易所的充币地址了
             filterAddresses.add(from);
        }
    }


    private boolean checkContract(Long blockNumber, String to){
        if(addressTypeMap.containsKey(to)){
            return addressTypeMap.get(to);
        }else{
            boolean isContract = false;
            try {
                isContract = centralNodeApi.isContract(to, blockNumber);
                addressTypeMap.put(to, isContract);
                //保存到db
                AddressType addressType = new AddressType();
                addressType.setContract(isContract);
                addressType.setAddress(to);
                //刷新cache
                addressTypeDao.save(addressType);
                return addressTypeMap.get(to);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void processMajor(TransferItem transferItem) {
        String from = transferItem.getTxFrom();
        String to = transferItem.getTxTo();

        // 相同地址不用分析
        if (from.equalsIgnoreCase(to)) {
            return;
        }

        // 排除地址不用分析，
        if (filterAddresses.contains(from) || filterAddresses.contains(to)) {
            log.debug("from：{} 或者 to：{} 是过滤地址", from, to);
            return;
        }
        // to是合约不用分析
        if(checkContract(transferItem.getBlockNumber(), to)){
            log.debug("交易：{} 的to：{} 是合约地址，不能作为关联地址等", transferItem.getTxHash(), transferItem.getTxTo());
            return;
        }

        String fromGroupName = majorAddressNameMap.get(from);
        String toGroupName = majorAddressNameMap.get(to);
        if (StringUtils.isNotEmpty(fromGroupName)  && StringUtils.isNotEmpty(toGroupName)) {
            if (StringUtils.equalsIgnoreCase(fromGroupName, toGroupName)) {
                return;
            }
            AddressGroup fromGroup = majorGroupMap.get(fromGroupName);
            AddressGroup toGroup = majorGroupMap.get(toGroupName);

            if (CollectionUtils.isNotEmpty(fromGroup.getInitialAddresses()) && CollectionUtils.isNotEmpty(toGroup.getInitialAddresses())) {
                //do nothing
            } else if (CollectionUtils.isNotEmpty(fromGroup.getInitialAddresses())) {
                // 将 toGroup 合并到 fromGroup中
                fromGroup.getRelatedAddresses().addAll(toGroup.getRelatedAddresses());

                // 删除 toGroup 中的地址到 groupId 的映射
                for (RelatedAddress address : toGroup.getRelatedAddresses()) {
                    majorAddressNameMap.remove(address.address());
                    majorAddressNameMap.put(address.address(), fromGroupName);
                }
                // 删除 toGroupId 到 addressGroup 的映射
                majorGroupMap.remove(toGroupName);

            } else if (CollectionUtils.isNotEmpty(toGroup.getInitialAddresses())) {
                // 将 fromGroup 合并到 toGroup中
                toGroup.getRelatedAddresses().addAll(fromGroup.getRelatedAddresses());

                // 删除 fromGroup 中的地址到 groupId 的映射
                for (RelatedAddress address : fromGroup.getRelatedAddresses()) {
                    majorAddressNameMap.remove(address.address());
                    majorAddressNameMap.put(address.address(), toGroupName);
                }
                // 删除 fromGroupId 到 addressGroup的映射
                majorGroupMap.remove(fromGroupName);
                // nothing to do
            } else {
                // 两个group都没有初始地址, 则将toGroup合并到fromGroup中
                fromGroup.getRelatedAddresses().addAll(toGroup.getRelatedAddresses());

                // 删除 toGroup 中的地址到 groupId 的映射
                for (RelatedAddress address : toGroup.getRelatedAddresses()) {
                    majorAddressNameMap.remove(address.address());
                    majorAddressNameMap.put(address.address(), fromGroupName);
                }
                // 删除 toGroupId 到 addressGroup 的映射
                majorGroupMap.remove(toGroupName);
            }
        } else if (StringUtils.isNotEmpty(fromGroupName)) {
            AddressGroup fromGroup = majorGroupMap.get(fromGroupName);
            fromGroup.addRelatedAddress(to, transferItem.getBlockNumber(), transferItem.getBlockTimestamp().getTime());
            majorAddressNameMap.put(to, fromGroupName);

        } else if (StringUtils.isNotEmpty(toGroupName)) {
            AddressGroup toGroup = majorGroupMap.get(toGroupName);
            toGroup.addRelatedAddress(from, transferItem.getBlockNumber(), transferItem.getBlockTimestamp().getTime());
            majorAddressNameMap.put(from, toGroupName);
        } else {
            AddressGroup newGroup = new AddressGroup();
            newGroup.setGroupName("group_" + from + "_" + to);
            majorGroupMap.put(newGroup.getGroupName(), newGroup);
            majorAddressNameMap.put(from, newGroup.getGroupName());
            majorAddressNameMap.put(to, newGroup.getGroupName());

            newGroup.addRelatedAddress(from, transferItem.getBlockNumber(), transferItem.getBlockTimestamp().getTime());
            newGroup.addRelatedAddress(to, transferItem.getBlockNumber(), transferItem.getBlockTimestamp().getTime());
        }
    }



    /*@Transactional
    @Override*/
    public void updateAddressGroup_old() {
        majorList.forEach(group -> {
            addressGroupDao.saveOrUpdate(group);
        });
    }

    @Transactional
    @Override
    public void updateAddressGroup(){
        List<AddressGroup> allAddressGroup = majorGroupMap.values().stream().toList();

        List<AddressGroup> majorList = allAddressGroup.stream().filter(group-> CollectionUtils.isNotEmpty(group.getInitialAddresses())).toList();

        addressGroupDao.saveBatch(majorList);
//        majorList.forEach(group -> {
//            addressGroupDao.saveBatch(group);
//        });
    }

    @Transactional
    @Override
    public void saveExchange() {
        List<FmisAssetAddress> exchangeRelatedAddressList = new ArrayList<>();
        exchangeRelatedAddressMap.forEach((groupId, relatedAddressSet) -> {
            relatedAddressSet.forEach((related) -> {
                FmisAssetAddress item = new FmisAssetAddress();
                item.setGroupId(groupId);
                item.setAddress(related.address());
                item.setTag("关联地址");
                item.setType(1);    //关联地址
                item.setRemark("关联地址");
                item.setRecBlock(related.blockNumber());
                item.setRecBlockTime(toDate(related.blockTime()));
                item.setBalance(BigDecimal.ZERO);
                item.setBalanceBlock(0L);
                item.setBalanceBlockDay(asDate(LocalDate.of(1970, 1, 1)));
                item.setProportion(BigDecimal.ZERO);
                exchangeRelatedAddressList.add(item);

            });
        });
        if (!exchangeRelatedAddressList.isEmpty()){
            fmisAssetAddressDao.saveBatch(exchangeRelatedAddressList, addressAnalysisConfig.getLimit());
        }
    }

    private static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    private static Date toDate(Long milliseconds) {
        Date date = new Date();
        date.setTime(milliseconds);
        return date;
    }


    @Override
    public Map<Integer, Set<String>> listExchangeCollectAddressMap() {
        Map<Integer, Set<String>> exchangeCollectAddressMap  = new HashMap<>();
        Set<Integer> exchangeGroupIdSet = fmisAssetGroupDao.listExchangeGroupId();
        for (Integer groupId : exchangeGroupIdSet) {
            Set<String> exchangeAddressSet = fmisAssetGroupDao.listExchangeCollectAddress(groupId);
            exchangeCollectAddressMap.put(groupId, exchangeAddressSet);
        }
        return exchangeCollectAddressMap;
    }

    @Override
    public void fetchBalance() {
        //查询所有fmis_addres_address地址在8300000的余额
        long curId = 0;

        List<FmisAssetAddress> assetAddressList = fmisAssetAddressDao.listAssetAddressByRange(curId, addressAnalysisConfig.getFetchBalanceBatchSize());

        log.debug("从链上获取地址的余额");
        int batch = 0;
        while (!assetAddressList.isEmpty()) {
            List<String> addressList = assetAddressList.stream().map(FmisAssetAddress::getAddress).collect(Collectors.toList());
            List<FmisAssetAddressDailyBalance> addressDailyBalanceList = new ArrayList<>();
            List<FmisAssetAddress> updateAssetAddressBalanceList = new ArrayList<>();
            try {
                log.debug("批量:{}", ++batch);
                List<BigInteger> balanceList = centralNodeApi.getMultiBalance(addressList, addressAnalysisConfig.getRecBlock());

                for (int i = 0; i<addressList.size(); i++){
                    FmisAssetAddressDailyBalance item = new FmisAssetAddressDailyBalance();
                    item.setAddress(addressList.get(i));
                    item.setBalance(new BigDecimal(balanceList.get(i)));
                    item.setBlockDay(addressAnalysisConfig.getRecBlockDay());
                    addressDailyBalanceList.add(item);

                    FmisAssetAddress updateAssetAddr = new FmisAssetAddress();
                    updateAssetAddr.setAddress(addressList.get(i));
                    updateAssetAddr.setBalance(new BigDecimal(balanceList.get(i)));
                    updateAssetAddr.setBalanceBlockDay(addressAnalysisConfig.getRecBlockDay());
                    updateAssetAddr.setBalanceBlock(addressAnalysisConfig.getRecBlock());
                    updateAssetAddressBalanceList.add(updateAssetAddr);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if(!updateAssetAddressBalanceList.isEmpty()) {
                log.debug("保存资产地址余额");
                fmisAssetAddressDao.updateBalanceBatch(updateAssetAddressBalanceList);
            }

            if(!addressDailyBalanceList.isEmpty()) {
                log.debug("保存资产地址每日余额");
                fmisAssetAddressDailyBalanceDao.saveOrUpdateOnDuplicatedBatch(addressDailyBalanceList);
            }

            curId = assetAddressList.get(assetAddressList.size() - 1).getId();
            assetAddressList = fmisAssetAddressDao.listAssetAddressByRange(curId, addressAnalysisConfig.getFetchBalanceBatchSize());
        }
    }

    private Map<String, Integer> convertExchangeCollectAddressGroupIdMap(Map<Integer, Set<String>> exchangeCollectAddressMap){
        Map<String, Integer> exchangeCollectAddressGroupIdMap = new HashMap<>();
        for (Map.Entry <Integer, Set<String>>  entry : exchangeCollectAddressMap.entrySet()) {
            for (String address : entry.getValue()) {
                exchangeCollectAddressGroupIdMap.put(address, entry.getKey());
            }
        }
        return exchangeCollectAddressGroupIdMap;
     }

     private void addExchangeRelatedAddress(Integer groupId, String relatedAddress, Long blockNumber, Long blockTime){
         Set<RelatedAddress> exchangeRelatedAddressSet = exchangeRelatedAddressMap.computeIfAbsent(groupId, k -> new HashSet<>());
         RelatedAddress item = new RelatedAddress(relatedAddress, blockNumber, blockTime);
         //没有识别出过才增加为交易所的关联地址，以第一次识别的为准
         if(!exchangeRelatedAddressSet.contains(item)){
             exchangeRelatedAddressSet.add(item);
         }
     }

    /*public List<BigInteger> getMultiBalance(Web3jService web3jService, List<String> addressSet, Long blockNumber) throws Exception {
        GetMultiBalanceRequest request = new GetMultiBalanceRequest(web3jService, addressSet, DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)));
        CompletableFuture<GetMultiBalanceResponse> responseFuture = request.sendAsync();
        GetMultiBalanceResponse response = responseFuture.get();
        List<BigInteger> balanceList =  response.getMultiBalance();
        return balanceList;
    }

    public Web3jService getWeb3jService(String web3jUrl) {

        WebSocketService wss = new WebSocketService(web3jUrl, true);

        try {
            wss.connect();
        } catch (Exception e) {
            log.error("中心节点无法WS连接", e);
            return null;
        }
        return wss;
    }*/
}
