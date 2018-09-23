package com.example.tony.myapplication;

import java.sql.Date;

public class EmpVO implements java.io.Serializable{
    public EmpVO() {

    }
    private String  emp_no;
    private String  branch_no;
    private String  emp_acnum;
    private String  emp_psw;
    private String  emp_name;
    private String  emp_gender;
    private String  emp_pos;
    private String  emp_tel;
    private String  emp_status;
    private Date    emp_credate;
    private boolean isEmp;
//	private byte[]  emp_photo;

    public boolean isEmp() {
        return isEmp;
    }

    public void setEmp(boolean emp) {
        isEmp = emp;
    }

    /**
     * @return the emp_no
     */
    public String getEmp_no() {
        return emp_no;
    }
    /**
     * @param emp_no the emp_no to set
     */
    public void setEmp_no(String emp_no) {
        this.emp_no = emp_no;
    }
    /**
     * @return the branch_no
     */
    public String getBranch_no() {
        return branch_no;
    }
    /**
     * @param branch_no the branch_no to set
     */
    public void setBranch_no(String branch_no) {
        this.branch_no = branch_no;
    }
    /**
     * @return the emp_acnum
     */
    public String getEmp_acnum() {
        return emp_acnum;
    }
    /**
     * @param emp_acnum the emp_acnum to set
     */
    public void setEmp_acnum(String emp_acnum) {
        this.emp_acnum = emp_acnum;
    }
    /**
     * @return the emp_psw
     */
    public String getEmp_psw() {
        return emp_psw;
    }
    /**
     * @param emp_psw the emp_psw to set
     */
    public void setEmp_psw(String emp_psw) {
        this.emp_psw = emp_psw;
    }
    /**
     * @return the emp_name
     */
    public String getEmp_name() {
        return emp_name;
    }
    /**
     * @param emp_name the emp_name to set
     */
    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }
    /**
     * @return the emp_gender
     */
    public String getEmp_gender() {
        return emp_gender;
    }
    /**
     * @param emp_gender the emp_gender to set
     */
    public void setEmp_gender(String emp_gender) {
        this.emp_gender = emp_gender;
    }
    /**
     * @return the emp_pos
     */
    public String getEmp_pos() {
        return emp_pos;
    }
    /**
     * @param emp_pos the emp_pos to set
     */
    public void setEmp_pos(String emp_pos) {
        this.emp_pos = emp_pos;
    }
    /**
     * @return the emp_tel
     */
    public String getEmp_tel() {
        return emp_tel;
    }
    /**
     * @param emp_tel the emp_tel to set
     */
    public void setEmp_tel(String emp_tel) {
        this.emp_tel = emp_tel;
    }
    /**
     * @return the emp_status
     */
    public String getEmp_status() {
        return emp_status;
    }
    /**
     * @param emp_status the emp_status to set
     */
    public void setEmp_status(String emp_status) {
        this.emp_status = emp_status;
    }
    /**
     * @return the emp_credate
     */
    public Date getEmp_credate() {
        return emp_credate;
    }
    /**
     * @param emp_credate the emp_credate to set
     */
    public void setEmp_credate(Date emp_credate) {
        this.emp_credate = emp_credate;
    }
    /**
     * @return the emp_photo
     */
//	public byte[] getEmp_photo() {
//		return emp_photo;
//	}
    /**
     * @param emp_photo the emp_photo to set
     */
//	public void setEmp_photo(byte[] emp_photo) {
//		this.emp_photo = emp_photo;
//	}


//	public EmpVO(String eMP_NO, String bRANCH_NO, String eMP_ACNUM, String eMP_PSW, String eMP_NAME, Integer eMP_GENDER,
//			String eMP_POS, Integer eMP_TEL, Integer eMP_STATUS, Date eMP_CREDATE, Byte eMP_PHOTO) {
//		super();
//		EMP_NO = eMP_NO;
//		BRANCH_NO = bRANCH_NO;
//		EMP_ACNUM = eMP_ACNUM;
//		EMP_PSW = eMP_PSW;
//		EMP_NAME = eMP_NAME;
//		EMP_GENDER = eMP_GENDER;
//		EMP_POS = eMP_POS;
//		EMP_TEL = eMP_TEL;
//		EMP_STATUS = eMP_STATUS;
//		EMP_CREDATE = eMP_CREDATE;
//		EMP_PHOTO = eMP_PHOTO;
//	}





}
