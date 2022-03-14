package com.seam.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seam.blog.dao.mapper.CategoryMapper;
import com.seam.blog.dao.pojo.Category;
import com.seam.blog.service.CateGoryService;
import com.seam.blog.vo.CategoryVo;
import com.seam.blog.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CateGoryServiceImpl implements CateGoryService {


    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public CategoryVo findCategoryById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }

    @Override
    public Result findAll() {
        List<Category> categories = this.categoryMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(copyList(categories));
    }

    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Category> categoryList = categoryMapper.selectList(lambdaQueryWrapper);
        return Result.success(copyList(categoryList));
    }

    @Override
    public Result findDetailById(Long id) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getId ,id);
        Category category = categoryMapper.selectOne(lambdaQueryWrapper);
        return Result.success(copy(category));
    }

    //    private Object copy(List<Category> categories) {
//        List<CategoryVo> categoryVoList = new ArrayList<>();
//        for (Category category : categories){
//            categoryVoList.add(copyList(category));
//        }
//        return categoryVoList;
//    }
//
//    private CategoryVo copyList(Category category) {
//        CategoryVo categoryVo = new CategoryVo();
//        BeanUtils.copyProperties(category,categoryVo);
//        return categoryVo;
//    }
    public CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }
    public List<CategoryVo> copyList(List<Category> categoryList){
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }
}
