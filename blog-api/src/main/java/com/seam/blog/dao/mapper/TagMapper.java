package com.seam.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seam.blog.dao.pojo.Tag;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 查询最热的标签前n条
     * @param limit
     * @return
     */
    List<Long> findHotsTagsIds(int limit);

    List<Tag> findTagsByTagIds(List<Long> tagIds);
}
