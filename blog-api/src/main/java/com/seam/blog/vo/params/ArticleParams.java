package com.seam.blog.vo.params;

import com.seam.blog.vo.CategoryVo;
import com.seam.blog.vo.TagVo;
import lombok.Data;

import java.util.List;

@Data
public class ArticleParams {


    private Long id;

    private ArticleBodyParam body;

    private CategoryVo category;

    private String summary;

    private List<TagVo> tags;

    private String title;
}
