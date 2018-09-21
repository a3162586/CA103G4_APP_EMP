package com.example.tony.myapplication;

public class AddressVO implements java.io.Serializable{
    String address;

    public AddressVO() {
    }

    public AddressVO(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
