package com.community.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Integer totalPage;

    public void setPagination(Integer totalCount, Integer page, Integer size) {
        //totalCount/size
        if (totalCount % size == 0) {   //totalCount是数据库有多少条数据，totalPage是数据库里的数据应该分多少页
            totalPage = totalCount/size;
        }
        else {
            totalPage = (totalCount/size) + 1;
        }
        //加一些限定page范围
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        this.page = page;
        //把该加进来的page加进来//当前page+左边3个+右边3个
        pages.add(page);
        for (int i = 1; i<= 3; i++) {
            if (page - i > 0) {
                pages.add(0,page - i);//往pages的头部加，即往当前页面的左边加page
            }
            if (page + i <= totalPage) {
                pages.add(page + i);//往当前页面右边加page
            }
        }


        //是否展示上一页
        if (page == 1) {
            showPrevious = false;
        }
        else {
            showPrevious = true;
        }
        //是否展示下一页
        if (page == totalPage) {
            showNext = false;
        }
        else {
            showNext = true;
        }
        //是否展示回到第一页，是否展示回到最后一页
        if (!pages.contains(1)) {
            showFirstPage = true;
        }
        else {
            showFirstPage = false;
        }
        if (!pages.contains(totalPage)) {
            showEndPage = true;
        }
        else {
            showEndPage = false;
        }
    }
}
