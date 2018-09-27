package com.example.tony.myapplication;

import java.util.List;

public class OrderformVO {
    private String order_no;
    private String dek_no;
    private String mem_no;
    private String branch_no;
    private String deliv_no;
    private Integer order_type;
    private Integer order_price;
    private Integer order_status;
    private String deliv_addres;
    private Integer order_pstatus;

    private List<OrderInvoiceVO> orderList;
    private MemberVO memVO;

    private List<OrderInvoiceWithMenuVO> orderList2;

    public List<OrderInvoiceWithMenuVO> getOrderList2() {
        return orderList2;
    }
    public void setOrderList2(List<OrderInvoiceWithMenuVO> orderList2) {
        this.orderList2 = orderList2;
    }

    public MemberVO getMemVO() {
        return memVO;
    }
    public void setMemVO(MemberVO memVO) {
        this.memVO = memVO;
    }

    public List<OrderInvoiceVO> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderInvoiceVO> orderList) {
        this.orderList = orderList;
    }

    public String getOrder_no() {
        return order_no;
    }
    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
    public String getDek_no() {
        return dek_no;
    }
    public void setDek_no(String dek_no) {
        this.dek_no = dek_no;
    }
    public String getMem_no() {
        return mem_no;
    }
    public void setMem_no(String mem_no) {
        this.mem_no = mem_no;
    }
    public String getBranch_no() {
        return branch_no;
    }
    public void setBranch_no(String branch_no) {
        this.branch_no = branch_no;
    }
    public String getDeliv_no() {
        return deliv_no;
    }
    public void setDeliv_no(String deliv_no) {
        this.deliv_no = deliv_no;
    }
    public Integer getOrder_type() {
        return order_type;
    }
    public void setOrder_type(Integer order_type) {
        this.order_type = order_type;
    }
    public Integer getOrder_price() {
        return order_price;
    }
    public void setOrder_price(Integer order_price) {
        this.order_price = order_price;
    }
    public Integer getOrder_status() {
        return order_status;
    }
    public void setOrder_status(Integer order_status) {
        this.order_status = order_status;
    }
    public String getDeliv_addres() {
        return deliv_addres;
    }
    public void setDeliv_addres(String deliv_addres) {
        this.deliv_addres = deliv_addres;
    }
    public Integer getOrder_pstatus() {
        return order_pstatus;
    }
    public void setOrder_pstatus(Integer order_pstatus) {
        this.order_pstatus = order_pstatus;
    }
}
