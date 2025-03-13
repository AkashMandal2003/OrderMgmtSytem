package com.jocata.oms.data.order.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.order.OrderItemDao;
import com.jocata.oms.datamodel.orders.entity.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class OrderItemDaoImpl implements OrderItemDao {

    private final HibernateConfig hibernateConfig;

    public OrderItemDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public OrderItem placeOrderItem(OrderItem orderItem) {
        return hibernateConfig.saveEntity(orderItem);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return hibernateConfig.findEntitiesByCriteria(OrderItem.class,"order.orderId",orderId);
    }
}
