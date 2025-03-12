package com.jocata.oms.data.order;

import com.jocata.oms.datamodel.orders.entity.OrderItem;

import java.util.List;

public interface OrderItemDao {

    OrderItem placeOrderItem(OrderItem orderItem);

    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

}
