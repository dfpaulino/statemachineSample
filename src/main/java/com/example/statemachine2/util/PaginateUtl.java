package com.example.statemachine2.util;

import java.util.List;

public class PaginateUtl {

    public static <T> List<T> getPageItems(List<T> list, int pageIdx, int pageSize) {

        int startIdx=pageIdx*pageSize;
        //is it last page
        int maxIdx=Math.round(list.size()/pageSize)>(pageIdx+1)?startIdx+pageSize:list.size();
        return list.subList(startIdx,maxIdx);
    }
}
