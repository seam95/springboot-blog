package com.seam.blog.service.impl;

import com.seam.blog.dao.mapper.ArticleBodyMapper;
import com.seam.blog.dao.pojo.ArticleBody;
import com.seam.blog.service.ArticleBodyService;
import com.seam.blog.vo.ArticleBodyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleBodyServiceImpl implements ArticleBodyService {

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Override
    public ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
//        BeanUtils.copyProperties(articleBody,articleBodyVo);
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }
}
