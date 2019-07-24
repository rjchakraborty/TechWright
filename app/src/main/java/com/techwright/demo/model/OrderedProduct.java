package com.techwright.demo.model;


import org.json.JSONObject;

public class OrderedProduct {

    private String brand_name;
    private String display_image;
    private String product_name;
    private String item_fit;
    private String colour_name;
    private String size;
    private String material;
    private double mrp;
    private double tax_percent;
    private String collection;
    private String currency;


    public OrderedProduct(JSONObject object, String currency) {
        if(object != null){
            this.setBrand_name(object.optString("brand_name"));
            this.setDisplay_image(object.optString("display_image"));
            this.setProduct_name(object.optString("product_name"));
            this.setItem_fit(object.optString("item_fit"));
            this.setColour_name(object.optString("colour_name"));
            this.setSize(object.optString("size"));
            this.setMaterial(object.optString("material"));
            this.setCollection(object.optString("collection"));
            this.setMrp(object.optDouble("mrp"));
            this.setTax_percent(object.optDouble("tax_percent"));
            this.currency = currency;
        }
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getTax_percent() {
        return tax_percent;
    }

    public void setTax_percent(double tax_percent) {
        this.tax_percent = tax_percent;
    }

    public String getItem_fit() {
        return item_fit;
    }

    public void setItem_fit(String item_fit) {
        this.item_fit = item_fit;
    }

    public String getColour_name() {
        return colour_name;
    }

    public void setColour_name(String colour_name) {
        this.colour_name = colour_name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDisplay_image() {
        return display_image;
    }

    public void setDisplay_image(String display_image) {
        this.display_image = display_image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}
