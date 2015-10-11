package com.somcrea.smartads.models;

/**
 * Created by rubengrafgarcia on 10/10/15.
 */
public class Offers {

    //region ATRIBUTS
    private String id;
    private String brand_id;
    private String clothes_id;
    private String style_id;
    private Integer discount;
    private String gender;
    private Integer minage;
    private Integer maxage;
    private String url;
    private String bluetooth_id;
    private String creation_time;
    //endregion

    //Constructor:
    public Offers(String id, String brand_id, String clothes_id, String style_id, Integer discount,
                   String gender, Integer minage, Integer maxage, String url, String bluetooth_id,
                   String creation_time)
    {
        this.setId(id);
        this.setBrand_id(brand_id);
        this.setClothes_id(clothes_id);
        this.setStyle_id(style_id);
        this.setDiscount(discount);
        this.setGender(gender);
        this.setMinage(minage);
        this.setMaxage(maxage);
        this.setUrl(url);
        this.setBluetooth_id(bluetooth_id);
        this.setCreation_time(creation_time);
    }

    //region GETTERS/SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getClothes_id() {
        return clothes_id;
    }

    public void setClothes_id(String clothes_id) {
        this.clothes_id = clothes_id;
    }

    public String getStyle_id() {
        return style_id;
    }

    public void setStyle_id(String style_id) {
        this.style_id = style_id;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getMinage() {
        return minage;
    }

    public void setMinage(Integer minage) {
        this.minage = minage;
    }

    public Integer getMaxage() {
        return maxage;
    }

    public void setMaxage(Integer maxage) {
        this.maxage = maxage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBluetooth_id() {
        return bluetooth_id;
    }

    public void setBluetooth_id(String bluetooth_id) {
        this.bluetooth_id = bluetooth_id;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }
    //endregion
}
