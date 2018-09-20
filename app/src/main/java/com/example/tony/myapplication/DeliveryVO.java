package com.example.tony.myapplication;

public class DeliveryVO implements java.io.Serializable {
    private String deliv_no;
    private String branch_no;
    private String emp_no;
    private String deliv_status;

    public String getDeliv_no() {
        return deliv_no;
    }

    public void setDeliv_no(String deliv_no) {
        this.deliv_no = deliv_no;
    }

    public String getBranch_no() {
        return branch_no;
    }

    public void setBranch_no(String branch_no) {
        this.branch_no = branch_no;
    }

    public String getEmp_no() {
        return emp_no;
    }

    public void setEmp_no(String emp_no) {
        this.emp_no = emp_no;
    }

    public String getDeliv_status() {
        return deliv_status;
    }

    public void setDeliv_status(String deliv_status) {
        this.deliv_status = deliv_status;
    }
}
