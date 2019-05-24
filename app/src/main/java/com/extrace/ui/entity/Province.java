package com.extrace.ui.entity;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

public class Province implements IPickerViewData{
    public String name;
    public List<Shi> city;
    public static class Shi{
        public String name;
        public List<String>area;

    }
    public String getPickerViewText() {
        return this.name;
    }
}
