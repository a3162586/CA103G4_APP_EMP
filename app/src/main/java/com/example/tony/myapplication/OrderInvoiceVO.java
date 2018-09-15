package com.example.tony.myapplication;

public class OrderInvoiceVO {
    private String invo_No;
    private String menu_Id;
    private int deleteIcon;

    public OrderInvoiceVO() {
    }

    public OrderInvoiceVO(String invo_No, String menu_Id, int deleteIcon) {
        this.invo_No = invo_No;
        this.menu_Id = menu_Id;
        this.deleteIcon = deleteIcon;
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
