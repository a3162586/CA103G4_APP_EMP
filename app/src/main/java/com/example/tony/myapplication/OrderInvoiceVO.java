package com.example.tony.myapplication;

public class OrderInvoiceVO implements java.io.Serializable{
    private String invo_No;
    private String menu_No;
    private String menu_Id;
    private Integer menu_Price;
    private int deleteIcon;

    public OrderInvoiceVO() {
    }

    public OrderInvoiceVO(String invo_No, String menu_No, String menu_Id, Integer menu_Price, int deleteIcon) {
        this.invo_No = invo_No;
        this.menu_No = menu_No;
        this.menu_Id = menu_Id;
        this.menu_Price = menu_Price;
        this.deleteIcon = deleteIcon;
    }

    public OrderInvoiceVO(String invo_No, String menu_No, String menu_Id, Integer menu_Price) {
        this.invo_No = invo_No;
        this.menu_No = menu_No;
        this.menu_Id = menu_Id;
        this.menu_Price = menu_Price;
    }

    public String getMenu_No() {
        return menu_No;
    }

    public void setMenu_No(String menu_No) {
        this.menu_No = menu_No;
    }

    public Integer getMenu_Price() {
        return menu_Price;
    }

    public void setMenu_Price(Integer menu_Price) {
        this.menu_Price = menu_Price;
    }

    public String getInvo_No() {
        return invo_No;
    }

    public void setInvo_No(String invo_No) {
        this.invo_No = invo_No;
    }

    public String getMenu_Id() {
        return menu_Id;
    }

    public void setMenu_Id(String menu_Id) {
        this.menu_Id = menu_Id;
    }

    public int getDeleteIcon() {
        return deleteIcon;
    }

    public void setDeleteIcon(int deleteIcon) {
        this.deleteIcon = deleteIcon;
    }
}
