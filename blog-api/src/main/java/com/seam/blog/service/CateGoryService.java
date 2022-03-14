package com.seam.blog.service;

import com.seam.blog.vo.CategoryVo;
import com.seam.blog.vo.Result;

import java.util.List;

public interface CateGoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result findDetailById(Long id);
}


