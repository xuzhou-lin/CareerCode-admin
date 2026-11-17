package com.lxz.careercode.admin.service.impl;

import com.lxz.careercode.admin.common.BizException;
import com.lxz.careercode.admin.dto.PageResult;
import com.lxz.careercode.admin.dto.WordBatchRequest;
import com.lxz.careercode.admin.dto.WordItemResponse;
import com.lxz.careercode.admin.dto.WordSaveRequest;
import com.lxz.careercode.admin.entity.admin.SensitiveWord;
import com.lxz.careercode.admin.mapper.admin.SensitiveWordMapper;
import com.lxz.careercode.admin.service.WordAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordAdminServiceImpl implements WordAdminService {

    private static final Map<Integer, String> CATEGORY_NAMES = Map.of(
            1, "政治敏感", 2, "色情低俗", 3, "广告导流", 4, "辱骂攻击", 5, "其他");

    /** 批量导入的分隔符：换行、逗号、分号都认 */
    private static final Pattern SPLITTER = Pattern.compile("[\\n\\r,，;；]+");

    private final SensitiveWordMapper wordMapper;

    @Override
    public PageResult<WordItemResponse> list(Integer category, Integer status, String keyword, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int offset = (safePage - 1) * safeSize;
        String trimmed = StringUtils.hasText(keyword) ? keyword.trim() : null;

        long total = wordMapper.countPage(category, status, trimmed);
        List<WordItemResponse> items = wordMapper.selectPage(category, status, trimmed, offset, safeSize)
                .stream()
                .map(this::toItem)
                .toList();
        return new PageResult<>(total, items);
    }

    @Override
    public void save(WordSaveRequest request) {
        String word = request.word().trim();

        if (request.id() == null) {
            // 1. 新增：词库不允许重复
            if (wordMapper.countByWord(word) > 0) {
                throw new BizException("这个词已经在词库里了");
            }
            wordMapper.insert(build(word, request.category(), request.level(), null));
            return;
        }

        // 2. 修改：词没变就不查重，变了才查
        SensitiveWord exist = wordMapper.selectById(request.id());
        if (exist == null) {
            throw new BizException("敏感词不存在");
        }
        if (!word.equals(exist.getWord()) && wordMapper.countByWord(word) > 0) {
            throw new BizException("这个词已经在词库里了");
        }

        SensitiveWord update = build(word, request.category(), request.level(), exist.getId());
        wordMapper.updateById(update);
    }

    @Override
    @Transactional
    public int batchImport(WordBatchRequest request) {
        // 1. 切分、去空、同批次内去重（用 LinkedHashSet 保留顺序）
        Set<String> candidates = new LinkedHashSet<>();
        for (String piece : SPLITTER.split(request.text())) {
            String word = piece.trim();
            if (!word.isEmpty() && word.length() <= 100) {
                candidates.add(word);
            }
        }
        if (candidates.isEmpty()) {
            return 0;
        }

        // 2. 一次查出已有词，避免逐个 count
        Set<String> existing = new java.util.HashSet<>(wordMapper.selectAllWords());
        int inserted = 0;
        for (String word : candidates) {
            if (existing.contains(word)) {
                continue;
            }
            wordMapper.insert(build(word, request.category(), request.level(), null));
            inserted++;
        }

        log.info("批量导入敏感词 候选={} 新增={}", candidates.size(), inserted);
        return inserted;
    }

    @Override
    public void updateStatus(Long wordId, Integer status) {
        if (status == null || (status != SensitiveWord.STATUS_ENABLED && status != SensitiveWord.STATUS_DISABLED)) {
            throw new BizException("状态只能是 0 或 1");
        }
        if (wordMapper.selectById(wordId) == null) {
            throw new BizException("敏感词不存在");
        }

        SensitiveWord update = new SensitiveWord();
        update.setId(wordId);
        update.setStatus(status);
        wordMapper.updateById(update);
    }

    @Override
    public void delete(Long wordId) {
        if (wordMapper.selectById(wordId) == null) {
            throw new BizException("敏感词不存在");
        }
        wordMapper.deleteById(wordId);
    }

    private SensitiveWord build(String word, Integer category, Integer level, Long id) {
        SensitiveWord entity = new SensitiveWord();
        entity.setId(id);
        entity.setWord(word);
        entity.setCategory(category == null ? SensitiveWord.CATEGORY_OTHER : category);
        entity.setLevel(level == null ? SensitiveWord.LEVEL_TIP : level);
        if (id == null) {
            entity.setStatus(SensitiveWord.STATUS_ENABLED);
            entity.setHitCount(0);
        }
        return entity;
    }

    private WordItemResponse toItem(SensitiveWord word) {
        return new WordItemResponse(
                word.getId(),
                word.getWord(),
                word.getCategory(),
                CATEGORY_NAMES.getOrDefault(word.getCategory(), "其他"),
                word.getLevel(),
                word.getStatus(),
                word.getHitCount(),
                word.getCreatedAt());
    }
}
