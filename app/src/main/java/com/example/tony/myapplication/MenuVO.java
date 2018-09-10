package com.example.tony.myapplication;

public class MenuVO {
    public static final Menu[] MENUS ={
        new Menu("叉燒咖哩拉麵",299,"48小時溫火慢熬精緻豚骨湯頭，搭配叉燒肉及溫泉蛋",R.drawable.ramen1),
        new Menu("叉燒原味豚骨拉麵",350,"回甘的牛肉湯頭，搭配空運來台紐西蘭牛肉",R.drawable.ramen2),
        new Menu("明太子拌麵",269,"川味麻辣湯頭，搭配辣味肉燥與叉燒肉",R.drawable.ramen3)
    };

    static class Menu {
        private String Menu_Id;
        private int Menu_Price;
        private String Menu_Intro;
        private int Menu_Photo;

        public Menu() {
        }

        public Menu(String menu_Id, int menu_Price, String menu_Intro, int menu_Photo) {
            Menu_Id = menu_Id;
            Menu_Price = menu_Price;
            Menu_Intro = menu_Intro;
            Menu_Photo = menu_Photo;
        }

        public String getMenu_Id() {
            return Menu_Id;
        }

        public void setMenu_Id(String menu_Id) {
            Menu_Id = menu_Id;
        }

        public int getMenu_Price() {
            return Menu_Price;
        }

        public void setMenu_Price(int menu_Price) {
            Menu_Price = menu_Price;
        }

        public String getMenu_Intro() {
            return Menu_Intro;
        }

        public void setMenu_Intro(String menu_Intro) {
            Menu_Intro = menu_Intro;
        }

        public int getMenu_Photo() {
            return Menu_Photo;
        }

        public void setMenu_Photo(int menu_Photo) {
            Menu_Photo = menu_Photo;
        }
    }
}
