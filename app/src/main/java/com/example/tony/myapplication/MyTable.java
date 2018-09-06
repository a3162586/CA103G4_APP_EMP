package com.example.tony.myapplication;

public class MyTable {

    public static final int TOTALTABLES = 100;
    static class Table {

        private int tableNo;
        private int tableImg;
        private String tableStatus;

        public Table() {
        }

        public Table(int tableNo, int tableImg, String tableStatus) {
            this.tableNo = tableNo;
            this.tableImg = tableImg;
            this.tableStatus = tableStatus;
        }

        public int getTableNo() {
            return tableNo;
        }

        public void setTableNo(int tableNo) {
            this.tableNo = tableNo;
        }

        public int getTableImg() {
            return tableImg;
        }

        public void setTableImg(int tableImg) {
            this.tableImg = tableImg;
        }

        public String getTableStatus() {
            return tableStatus;
        }

        public void setTableStatus(String tableStatus) {
            this.tableStatus = tableStatus;
        }
    }

}
