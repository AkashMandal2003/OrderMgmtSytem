package com.jocata.oms.inventory.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocata.oms.datamodel.orders.form.OrderForm;
import com.jocata.oms.datamodel.orders.form.OrderItemForm;
import com.jocata.oms.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private final InventoryService inventoryService;

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    public void processInventory(String order) {
        ObjectMapper mapper = new ObjectMapper();
        OrderForm orderForm;
        try {
            orderForm = mapper.readValue(order, OrderForm.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        for (OrderItemForm itemForm:orderForm.getOrderItems()){
            inventoryService.reserveStock(Integer.valueOf(itemForm.getProduct().getProductId()), itemForm.getQuantity());
        }
        System.out.println("Inventory reserved with order id:" + orderForm.getOrderId());
    }

}
