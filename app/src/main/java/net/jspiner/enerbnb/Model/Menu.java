package net.jspiner.enerbnb.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2015 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project MySmartRestaurant
 * @since 2015. 11. 14.
 */
public class Menu {
    public List<SubMenu> items;
    public int res;
    public String menu;

    public Menu(String menu, int res){
        this.menu = menu;
        this.res = res;
        this.items = new ArrayList<>();
    }
}
