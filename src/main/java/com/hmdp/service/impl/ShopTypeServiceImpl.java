package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryAll() {
        String key = "shopType";
        List<ShopType> shopTypeList = new ArrayList<>();
        List<String> shopTypeJSONList = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (shopTypeJSONList != null && !shopTypeJSONList.isEmpty()) {
            for (String shopTypeJSON : shopTypeJSONList) {
                ShopType shopType = JSONUtil.toBean(shopTypeJSON, ShopType.class);
                shopTypeList.add(shopType);
            }
            return Result.ok(shopTypeList);
        }
        shopTypeList = query().orderByAsc("sort").list();
        if (shopTypeList == null || shopTypeList.isEmpty()) {
            return Result.fail("NO SHOPTYPE");
        }
        for (ShopType shopType : shopTypeList) {
            stringRedisTemplate.opsForList().rightPush(key, JSONUtil.toJsonStr(shopType));
        }
        return Result.ok(shopTypeList);
    }
}
