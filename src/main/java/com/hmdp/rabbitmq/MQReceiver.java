package com.hmdp.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hmdp.config.RabbitMQConfig;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IVoucherOrderService voucherOrderService;

    @Autowired
    private ISeckillVoucherService seckillVoucherService;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveSeckillMessage(String message) {
        log.info("receive seckill message: {}", message);
        VoucherOrder voucherOrder = JSON.parseObject(message, VoucherOrder.class);
        Long voucherId = voucherOrder.getVoucherId();
        Long userId = voucherOrder.getUserId();
        int count = voucherOrderService.query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            log.error("already purchased");
            return;
        }
        boolean update = seckillVoucherService.update()
                .setSql("stock = stock - 1").gt("stock", 0)
                .eq("voucher_id", voucherId)
                .update();
        if (!update) {
            log.error("stock not enough");
            return;
        }
        voucherOrderService.save(voucherOrder);
    }
}
