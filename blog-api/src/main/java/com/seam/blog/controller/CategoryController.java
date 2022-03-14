package com.seam.blog.controller;


import com.seam.blog.service.CateGoryService;
import com.seam.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CateGoryService cateGoryService;

    @GetMapping
    public Result ListCategory(){
        return cateGoryService.findAll();
    }


    @GetMapping("detail")
    public Result categoryDetail(){
        return cateGoryService.findAllDetail();
    }

    @GetMapping("detail/{id}")
    public Result categoryDetail(@PathVariable("id") Long id){
        return cateGoryService.findDetailById(id);
    }
}
