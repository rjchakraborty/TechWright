package com.techwright.demo.model;

import org.json.JSONObject;

public class Order {

    private String order_no;
    private int quantity;
    private String created_at;
    private String currency;
    private String razorpay_order_id;
    private String store;
    private double gross_amount;
    private double net_amount;
    private double total_tax;
    private String tracking_id;
    private String status;
    private OrderedProduct product;

    public Order(JSONObject object) {
        if(object != null){
            this.setOrder_no(object.optString("order_no"));
            this.setQuantity(object.optInt("quantity"));
            this.setCreated_at(object.optString("created_at"));
            this.setCurrency(object.optString("currency"));
            this.setRazorpay_order_id(object.optString("razorpay_order_id"));
            this.setStore(object.optString("store"));
            this.setGross_amount(object.optDouble("gross_amount"));
            this.setNet_amount(object.optDouble("net_amount"));
            this.setTotal_tax(object.optDouble("total_tax"));
            this.setTracking_id(object.optString("tracking_id"));
            this.setStatus(object.optString("status"));
        }
    }

    public OrderedProduct getProduct() {
        return product;
    }

    public void setProduct(OrderedProduct product) {
        this.product = product;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }

    public void setRazorpay_order_id(String razorpay_order_id) {
        this.razorpay_order_id = razorpay_order_id;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public double getGross_amount() {
        return gross_amount;
    }

    public void setGross_amount(double gross_amount) {
        this.gross_amount = gross_amount;
    }

    public double getNet_amount() {
        return net_amount;
    }

    public void setNet_amount(double net_amount) {
        this.net_amount = net_amount;
    }

    public double getTotal_tax() {
        return total_tax;
    }

    public void setTotal_tax(double total_tax) {
        this.total_tax = total_tax;
    }

    public String getTracking_id() {
        return tracking_id;
    }

    public void setTracking_id(String tracking_id) {
        this.tracking_id = tracking_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClassPojo [order_no = " + order_no + ", quantity = " + quantity + ", created_at = " + created_at + ", currency = " + currency + ", razorpay_order_id = " + razorpay_order_id + ", store = " + store + ", gross_amount = " + gross_amount + ", net_amount = " + net_amount + ", total_tax = " + total_tax + ", tracking_id = " + tracking_id + ", status = " + status + "]";
    }
}

