package com.jocata.oms.data.order.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.order.OrderDao;
import com.jocata.oms.datamodel.orders.entity.OrderDetails;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDaoImpl implements OrderDao {

    private final HibernateConfig hibernateConfig;

    public OrderDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public OrderDetails placeOrder(OrderDetails orderDetails) {
        return hibernateConfig.saveEntity(orderDetails);
    }

    @Override
    public OrderDetails getOrderById(Integer orderId) {
        return hibernateConfig.findEntityById(OrderDetails.class, orderId);
    }

    @Override
    public OrderDetails updateOrder(OrderDetails orderDetails) {
        return hibernateConfig.updateEntity(orderDetails);
    }
}
