package com.lxz.careercode.admin.service.impl;

import com.lxz.careercode.admin.common.AdminContext;
import com.lxz.careercode.admin.common.BizException;
import com.lxz.careercode.admin.common.ClientIpResolver;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.TagItemResponse;
import com.lxz.careercode.admin.dto.TagSaveRequest;
import com.lxz.careercode.admin.entity.admin.AdminOperationLog;
import com.lxz.careercode.admin.entity.biz.Tag;
import com.lxz.careercode.admin.mapper.admin.AdminOperationLogMapper;
import com.lxz.careercode.admin.mapper.biz.TagMapper;
import com.lxz.careercode.admin.service.TagAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagAdminServiceImpl implements TagAdminService {

    private static final Map<Integer, String> TYPE_NAMES = Map.of(
            1, "公司", 2, "岗位", 3, "技术栈", 4, "学校",
            5, "年份", 6, "求职阶段", 7, "自定义");

    private final TagMapper tagMapper;
    private final AdminOperationLogMapper operationLogMapper;

    @Override
    public PageResult<TagItemResponse> list(Integer type, String keyword, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int offset = (safePage - 1) * safeSize;
        String trimmed = StringUtils.hasText(keyword) ? keyword.trim() : null;

        long total = tagMapper.countPage(type, trimmed);
        List<TagItemResponse> items = tagMapper.selectPage(type, trimmed, offset, safeSize)
                .stream()
                .map(this::toItem)
                .toList();
        return new PageResult<>(total, items);
    }

    @Override
    public void save(TagSaveRequest request) {
        String name = request.name().trim();

        // 1. 同类型下不能重名，改名时把自己排除
        if (tagMapper.countSameName(request.type(), name, request.id()) > 0) {
            throw new BizException("同类型下已经有同名标签");
        }

        if (request.id() == null) {
            // 2. 新增
            Tag tag = new Tag();
            tag.setName(name);
            tag.setType(request.type());
            tag.setRefId(request.refId());
            tag.setUseCount(0);
            tag.setStatus(request.status() == null ? Tag.STATUS_NORMAL : request.status());
            tagMapper.insert(tag);
            writeLog("tag_create", tag.getId(), name);
            return;
        }

        // 3. 修改：改名或改状态，其余字段不覆盖
        Tag exist = tagMapper.selectById(request.id());
        if (exist == null) {
            throw new BizException("标签不存在");
        }
        Tag update = new Tag();
        update.setId(exist.getId());
        update.setName(name);
        update.setType(request.type());
        if (request.refId() != null) {
            update.setRefId(request.refId());
        }
        if (request.status() != null) {
            update.setStatus(request.status());
        }
        tagMapper.updateById(update);
        writeLog("tag_edit", exist.getId(), name);
    }

    @Override
    public void updateStatus(Long tagId, Integer status) {
        if (status == null || (status != Tag.STATUS_NORMAL && status != Tag.STATUS_DISABLED)) {
            throw new BizException("状态只能是 0 或 1");
        }

        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BizException("标签不存在");
        }

        Tag update = new Tag();
        update.setId(tagId);
        update.setStatus(status);
        tagMapper.updateById(update);

        writeLog(status == Tag.STATUS_DISABLED ? "tag_disable" : "tag_enable", tagId, tag.getName());
    }

    @Override
    public void delete(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BizException("标签不存在");
        }

        // 被帖子引用的标签不能物理删，否则帖子上的标签会变成悬空 id
        long refs = tagMapper.countPostRefs(tagId);
        if (refs > 0) {
            throw new BizException("该标签已被 " + refs + " 篇内容使用，只能禁用不能删除");
        }

        tagMapper.deleteById(tagId);
        writeLog("tag_delete", tagId, tag.getName());
    }

    private void writeLog(String action, Long tagId, String name) {
        Long adminId = AdminContext.getId();
        if (adminId == null) {
            return;
        }
        try {
            AdminOperationLog entry = new AdminOperationLog();
            entry.setAdminUserId(adminId);
            entry.setModule("tag");
            entry.setAction(action);
            entry.setTargetId(tagId);
            entry.setTargetTitle(name);
            entry.setIp(ClientIpResolver.currentIp());
            operationLogMapper.insert(entry);
        } catch (Exception e) {
            log.warn("写操作日志失败 tagId={}", tagId, e);
        }
    }

    private TagItemResponse toItem(Tag tag) {
        return new TagItemResponse(
                tag.getId(),
                tag.getName(),
                tag.getType(),
                TYPE_NAMES.getOrDefault(tag.getType(), "其他"),
                tag.getUseCount(),
                tag.getStatus(),
                tag.getCreatedAt());
    }
}
