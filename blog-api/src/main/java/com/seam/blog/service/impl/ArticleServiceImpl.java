package com.seam.blog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seam.blog.dao.dos.Archives;
import com.seam.blog.dao.mapper.ArticleBodyMapper;
import com.seam.blog.dao.mapper.ArticleMapper;
import com.seam.blog.dao.mapper.ArticleTagMapper;
import com.seam.blog.dao.mapper.CategoryMapper;
import com.seam.blog.dao.pojo.Article;
import com.seam.blog.dao.pojo.ArticleBody;
import com.seam.blog.dao.pojo.ArticleTag;
import com.seam.blog.dao.pojo.SysUser;
import com.seam.blog.service.*;
import com.seam.blog.utils.UserThreadLocal;
import com.seam.blog.vo.ArticleBodyVo;
import com.seam.blog.vo.ArticleVo;
import com.seam.blog.vo.Result;
import com.seam.blog.vo.TagVo;
import com.seam.blog.vo.params.ArticleParams;
import com.seam.blog.vo.params.PageParams;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService{

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleBodyService articleBodyService;

    @Autowired
    private CateGoryService cateGoryService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

//    @Override
//    public Result listArticle(PageParams pageParams) {
//        /**
//         * 1. ????????????article????????????
//         */
//        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//        if (pageParams.getCategoryId() != null){
//            queryWrapper.eq(Article::getCategoryId , pageParams.getCategoryId());
//        }
//        List<Long> articleIdList = new ArrayList<>();
//        if (pageParams.getTagId() != null){
//            LambdaQueryWrapper<ArticleTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(ArticleTag::getTagId , pageParams.getTagId());
//            List<ArticleTag> articleTagList = articleTagMapper.selectList(lambdaQueryWrapper);
//            for (ArticleTag articleTag : articleTagList) {
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if (articleIdList.size() > 0){
//                queryWrapper.in(Article::getId , articleIdList);
//            }
//        }
//
////        List<Long> articleIdList = new ArrayList<>();
////        if (pageParams.getTagId() != null){
////            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
////            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
////            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
////            for (ArticleTag articleTag : articleTags) {
////                articleIdList.add(articleTag.getArticleId());
////            }
////            if (articleIdList.size() > 0){
////                queryWrapper.in(Article::getId,articleIdList);
////            }
////        }
//
//        //??????????????????
////      queryWrapper.orderByDesc(Article::getWeight);
////      order by create_date desc
//        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records = articlePage.getRecords();
//        //??????????????????List??????
//        List<ArticleVo> articleVoList = copyList(records ,true ,true);
//        return Result.success(articleVoList);
//    }


    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = this.articleMapper.listArticle(page,pageParams.getCategoryId(),pageParams.getTagId(),pageParams.getYear(),pageParams.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true));
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id ,title from article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        //select id ,title from article order by create_date desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success((copyList(articles,false,false)));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    @Override
    public Result findArticleById(Long id) {
        /**
         * 1. ??????id?????? ????????????
         * 2. ??????bodyId???categoryid ??????????????????
         */
        Article article = articleMapper.selectById(id);
        ArticleVo articleVo = copy(article ,true,true,true,true);
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //????????? ????????????????????????????????????????????? ?????????????????????
        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    @Override
    @Transactional
    public Result publish(ArticleParams articleParam) {
        SysUser sysUser = UserThreadLocal.get();

        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setCategoryId(Long.valueOf(articleParam.getCategory().getId()));
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common);
        article.setBodyId(-1L);
        this.articleMapper.insert(article);

        //tags
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                this.articleTagMapper.insert(articleTag);
            }
        }
        ArticleBody articleBody = new ArticleBody();
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBody.setArticleId(article.getId());
        articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(article.getId());
        return Result.success(articleVo);
    }


    private List<ArticleVo> copyList(List<Article> records , boolean isTag , boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        //for??????,????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        for (Article record : records){
            articleVoList.add(copy(record , isTag, isAuthor,false,false));
        }
        return articleVoList;
    }


    private List<ArticleVo> copyList(List<Article> records , boolean isTag , boolean isAuthor,boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        //for??????,????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        for (Article record : records){
            articleVoList.add(copy(record , isTag, isAuthor,isBody,isCategory));
        }
        return articleVoList;
    }

    private ArticleVo copy(Article article , boolean isTag , boolean isAuthor,boolean isBody, boolean isCategory
    ){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //??????????????????????????????????????????????????????
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            Long articleId = article.getId();
            articleVo.setAuthor(sysUserService.findUserByUser(articleId).getNickname());
        }
        if (isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(articleBodyService.findArticleBodyById(bodyId));
        }
        if (isCategory){
            Long categoryId = article.getCategoryId();
            articleVo.setCategorys(cateGoryService.findCategoryById(categoryId));
        }

        return articleVo;
    }

}
