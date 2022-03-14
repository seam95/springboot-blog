package com.seam.blog.service;

import com.seam.blog.vo.ArticleVo;
import com.seam.blog.vo.Result;
import com.seam.blog.vo.params.ArticleParams;
import com.seam.blog.vo.params.PageParams;

public interface ArticleService {

    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    Result hotArticle(int limit);

    Result newArticles(int limit);

    Result listArchives();

    Result findArticleById(Long id);

    Result publish(ArticleParams articleParams);
}
