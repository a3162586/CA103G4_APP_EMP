package com.example.tony.myapplication;


public class OrderInvoiceWithMenuVO {
    private String invo_no;
    private String order_no;
    private String menu_no;
    private String custom_no;
    private Integer invo_status;

    private MenuVO menuVO;

    public MenuVO getMenuVO() {
        return menuVO;
    }

    public void setMenuVO(MenuVO menuVO) {
        this.menuVO = menuVO;
    }

    public String getInvo_no() {
        return invo_no;
    }

    public void setInvo_no(String invo_no) {
        this.invo_no = invo_no;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getMenu_no() {
        return menu_no;
    }

    public void setMenu_no(String menu_no) {
        this.menu_no = menu_no;
    }

    public String getCustom_no() {
        return custom_no;
    }

    public void setCustom_no(String custom_no) {
        this.custom_no = custom_no;
    }

    public Integer getInvo_status() {
        return invo_status;
    }

    public void setInvo_status(Integer invo_status) {
        this.invo_status = invo_status;
    }
}

