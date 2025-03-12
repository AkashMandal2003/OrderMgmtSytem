package com.jocata.oms.data.order;

import com.jocata.oms.datamodel.orders.entity.OrderDetails;

public interface OrderDao {

    OrderDetails placeOrder(OrderDetails orderDetails);

    OrderDetails getOrderById(Integer orderId);

    OrderDetails updateOrder(OrderDetails orderDetails);
}
