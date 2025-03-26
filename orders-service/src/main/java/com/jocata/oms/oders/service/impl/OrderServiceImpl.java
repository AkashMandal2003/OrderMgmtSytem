package com.jocata.oms.oders.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocata.oms.common.response.GenericResponsePayload;
import com.jocata.oms.data.order.OrderDao;
import com.jocata.oms.data.order.OrderItemDao;
import com.jocata.oms.datamodel.orders.entity.OrderDetails;
import com.jocata.oms.datamodel.orders.entity.OrderItem;
import com.jocata.oms.datamodel.orders.enums.OrderStatus;
import com.jocata.oms.datamodel.orders.form.OrderForm;
import com.jocata.oms.datamodel.orders.form.OrderItemForm;
import com.jocata.oms.datamodel.product.form.ProductForm;
import com.jocata.oms.datamodel.um.form.UserForm;
import com.jocata.oms.oders.service.OrderService;
import com.jocata.oms.publisher.OrderEventPublisher;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final RestTemplate restTemplate;
    private final OrderEventPublisher orderEventPublisher;
    private final ObjectMapper objectMapper;

    private static final String USER_URI = "http://localhost:8081/api/v1/users/user/";
    private static final String PRODUCT_URI = "http://localhost:8085/products/product/";

    private static final String USER_SERVICE = "userService";
    private static final String PRODUCT_SERVICE = "productService";

    public OrderServiceImpl(OrderDao orderDao, OrderItemDao orderItemDao, RestTemplate restTemplate, OrderEventPublisher orderEventPublisher, ObjectMapper objectMapper) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.restTemplate = restTemplate;
        this.orderEventPublisher = orderEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public OrderForm createOrder(OrderForm orderForm) {
        try {
            OrderDetails order = buildOrderDetails(orderForm);
            List<OrderItem> orderItems = buildOrderItems(orderForm);

            order.setTotalAmount(calculateTotalAmount(orderItems));
            order.setOrderStatus(OrderStatus.PENDING);

            OrderDetails savedOrder = orderDao.placeOrder(order);
            List<OrderItemForm> savedOrderItemsFormList = saveOrderItems(orderItems, savedOrder);

            OrderForm orderResponse = buildOrderForm(savedOrder, savedOrderItemsFormList);
            String jsonString = objectMapper.writeValueAsString(orderResponse);

            //kafka event creation
            orderEventPublisher.publishOrderEvent(jsonString);

            return orderResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating order");
        }
    }

    @Override
    public String cancelOrder(Integer orderId) {
        try {
            OrderDetails orderById = orderDao.getOrderById(orderId);
            orderById.setOrderStatus(OrderStatus.CANCELLED);
            orderDao.updateOrder(orderById);
            return "Order cancelled";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Order not cancelled";
    }

    @Override
    public OrderForm getOrderById(Integer orderId) {

        try {
            OrderDetails orderDetails = orderDao.getOrderById(orderId);
            if (orderDetails == null) {
                throw new RuntimeException("Order not found with ID: " + orderId);
            }
            List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrderId(orderId);
            List<OrderItemForm> orderItemForms = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                orderItemForms.add(convertToOrderItemForm(orderItem));
            }
            return buildOrderForm(orderDetails, orderItemForms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OrderForm();
    }

    private OrderDetails buildOrderDetails(OrderForm orderForm) {
        OrderDetails order = new OrderDetails();
        order.setCustomerId(Integer.parseInt(orderForm.getCustomerId()));
        order.setDeliveryAddress(orderForm.getCustomerAddress());
        order.setOrderDate(Timestamp.from(Instant.now()));
        return order;
    }

    private List<OrderItem> buildOrderItems(OrderForm orderForm) {
        List<OrderItem> orderItemEntities = new ArrayList<>();
        if (orderForm.getOrderItems() != null) {
            for (OrderItemForm item : orderForm.getOrderItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(Integer.parseInt(item.getProduct().getProductId()));
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(new BigDecimal(item.getProduct().getPrice()));
                orderItemEntities.add(orderItem);
            }
        }
        return orderItemEntities;
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItemForm> saveOrderItems(List<OrderItem> orderItems, OrderDetails savedOrder) {
        List<OrderItemForm> savedOrderItemsFormList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
            OrderItem savedOrderItem = orderItemDao.placeOrderItem(orderItem);
            savedOrderItemsFormList.add(convertToOrderItemForm(savedOrderItem));
        }
        return savedOrderItemsFormList;
    }

    private OrderItemForm convertToOrderItemForm(OrderItem savedOrderItem) {
        OrderItemForm savedOrderItemForm = new OrderItemForm();

        ProductForm product;
        try {
            product = getProduct(savedOrderItem.getProductId());
        } catch (Exception e) {
            product = fallbackGetProduct(savedOrderItem.getProductId(), e);
        }

        savedOrderItemForm.setProduct(product);
        savedOrderItemForm.setQuantity(savedOrderItem.getQuantity());
        return savedOrderItemForm;
    }

    private OrderForm buildOrderForm(OrderDetails orderDetails, List<OrderItemForm> savedOrderItemsFormList) {
        UserForm userForm;
        try {
            userForm = getCustomer(orderDetails.getCustomerId());
        } catch (Exception e) {
            userForm = fallbackGetCustomer(orderDetails.getCustomerId(), e);
        }

        OrderForm savedOrderForm = new OrderForm();
        savedOrderForm.setOrderId(String.valueOf(orderDetails.getOrderId()));
        savedOrderForm.setCustomerId(userForm.getUserId());
        savedOrderForm.setCustomerName(userForm.getFullName());
        savedOrderForm.setCustomerEmail(userForm.getEmail());
        savedOrderForm.setCustomerPhone(userForm.getPhone());
        savedOrderForm.setCustomerAddress(orderDetails.getDeliveryAddress());
        savedOrderForm.setTotalPrice(orderDetails.getTotalAmount().toString());
        savedOrderForm.setOrderStatus(orderDetails.getOrderStatus().name());
        savedOrderForm.setOrderDate(orderDetails.getOrderDate().toString());
        savedOrderForm.setOrderItems(savedOrderItemsFormList);

        return savedOrderForm;
    }


    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackGetCustomer")
    public UserForm getCustomer(Integer customerId) {
        try {
            String userUrl = USER_URI + customerId;
            ResponseEntity<GenericResponsePayload<UserForm>> response = restTemplate.exchange(
                    userUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<GenericResponsePayload<UserForm>>() {
                    }
            );
            return response.getBody().getData();
        } catch (Exception e) {
            throw e;
        }

    }

    @CircuitBreaker(name = PRODUCT_SERVICE, fallbackMethod = "fallbackGetProduct")
    public ProductForm getProduct(Integer productId) {
        try {
            String productUrl = PRODUCT_URI + productId;
            ResponseEntity<GenericResponsePayload<ProductForm>> response = restTemplate.exchange(
                    productUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<GenericResponsePayload<ProductForm>>() {
                    }
            );
            return response.getBody().getData();
        } catch (Exception e) {
            throw e;
        }

    }

    private UserForm fallbackGetCustomer(Integer customerId, Throwable t) {
        System.err.println("Fallback triggered for getCustomer: " + t.getMessage());
        UserForm defaultUser = new UserForm();
        defaultUser.setUserId("0");
        defaultUser.setFullName("Unknown User");
        defaultUser.setEmail("unknown@example.com");
        defaultUser.setPhone("0000000000");
        return defaultUser;
    }

    private ProductForm fallbackGetProduct(Integer productId,Throwable t) {
        System.err.println("Fallback triggered for getProduct: " + t.getMessage());
        ProductForm defaultProduct = new ProductForm();
        defaultProduct.setProductId("0");
        defaultProduct.setName("Unknown Product");
        defaultProduct.setPrice("0.00");
        return defaultProduct;
    }
}
