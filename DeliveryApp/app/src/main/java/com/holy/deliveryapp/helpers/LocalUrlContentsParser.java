package com.holy.deliveryapp.helpers;

import com.holy.deliveryapp.models.LocalUrlContents;
import com.holy.deliveryapp.models.Menu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LocalUrlContentsParser {

    public static LocalUrlContents parse(String html) {

        String status = "";
        List<String> timeList = new ArrayList<>();
        List<Menu> menuList = new ArrayList<>();

        List<String> menuNameList = new ArrayList<>();
        List<Integer> menuPriceList = new ArrayList<>();

        Document doc = Jsoup.parse(html);

        // 메뉴 파싱

        Elements menuNameElemList = doc.select(".name_menu");
        for (Element menuNameElem : menuNameElemList) {
            menuNameList.add(menuNameElem.text().trim());
        }

        Elements menuPriceElemList = doc.select(".price_menu");
        for (Element menuPriceElem : menuPriceElemList) {
            String strPrice = menuPriceElem.text();
            String[] strPriceArray = strPrice.split(":");
            String strPriceNumber;
            if (strPriceArray.length > 1) {
                strPriceNumber = strPriceArray[1].trim();
            } else {
                strPriceNumber = strPriceArray[0].trim();
            }
            try {
                strPriceNumber = strPriceNumber.replace(",", "");
                int price = Integer.parseInt(strPriceNumber);
                menuPriceList.add(price);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < menuNameList.size(); i++) {
            if (i < menuPriceList.size()) {
                String name = menuNameList.get(i);
                int price = menuPriceList.get(i);
                menuList.add(new Menu(name, price));
            }
        }

        return new LocalUrlContents(status, timeList, menuList);
    }

}
