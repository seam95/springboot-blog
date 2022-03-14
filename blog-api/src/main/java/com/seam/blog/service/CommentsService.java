package com.seam.blog.service;

import com.seam.blog.vo.Result;
import com.seam.blog.vo.params.CommentParam;

public interface CommentsService {
    /**
     * 根据id查询文章评论
     * @return
     */

    Result commentsByArticleId(Long articleId);

    Result comment(CommentParam commentParam);
}
