package com.kanemart.dto;

public class CreateOrderRequest {

    private String customerName;

    private String customerMobile;

    private Long productId;

    private int quantity;

    public CreateOrderRequest() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(
            String customerName) {

        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(
            String customerMobile) {

        this.customerMobile = customerMobile;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(
            Long productId) {

        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(
            int quantity) {

        this.quantity = quantity;
    }
}