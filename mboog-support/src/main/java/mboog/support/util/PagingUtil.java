package mboog.support.util;

import mboog.support.bean.*;
import mboog.support.example.AbstractExample;
import mboog.support.example.AbstractGeneratedCriteria;
import mboog.support.example.CInterface;
import mboog.support.example.ExampleConstants;
import mboog.support.exceptions.PagingException;
import mboog.support.mapper.ReadMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页工具类
 *
 * @author LiYi
 */
public class PagingUtil {

    /**
     * 精简分页
     *
     * @param dataSort     数据排序方式 AES or DESC
     * @param direction    动作 PREV or NEXT
     * @param readMapper   Mapper
     * @param example      Example
     * @param c            Example.C
     * @param function     Example.C value function
     * @param count        Query count
     * @param queryId      Query Id value
     * @param <PrimaryKey>
     * @param <Model>
     * @param <Criteria>
     * @param <C>
     * @param <Example>
     * @return
     */
    public static <PrimaryKey, Model, Criteria extends AbstractGeneratedCriteria<?>, C extends Enum<C>, Example extends AbstractExample<Example, Criteria, C>> LitePage<Model> litePage(
            DataSort dataSort,
            Direction direction,
            ReadMapper<PrimaryKey, Model, Example> readMapper,
            Example example,
            C c,
            Function<Model, Object> function,
            Integer count,
            Object queryId
    ) {

        Page<Model> page = totalPage(dataSort, direction, false, readMapper, example, c, function, count, queryId, null);
        page.setHasNext(null);
        page.setHasPrev(null);
        page.setCurrent(null);
        page.setFirst(null);
        page.setLast(null);
        page.setPageNo(null);
        return page;
    }


    /**
     * 总计分页
     *
     * @param dataSort     数据排序方式 AES or DESC
     * @param direction    动作 PREV or NEXT
     * @param queryTotal   是否查询总记录条数
     * @param readMapper   Mapper
     * @param example      Example
     * @param c            Example.C
     * @param function     Example.C value function
     * @param count        Query count
     * @param queryId      Query Id value
     * @param pageNo       Page number
     * @param <PrimaryKey>
     * @param <Model>
     * @param <Criteria>
     * @param <C>
     * @param <Example>
     * @return
     */
    public static <PrimaryKey, Model, Criteria extends AbstractGeneratedCriteria<?>, C extends Enum<C>, Example extends AbstractExample<Example, Criteria, C>> Page<Model> totalPage(
            DataSort dataSort,
            Direction direction,
            boolean queryTotal,
            ReadMapper<PrimaryKey, Model, Example> readMapper,
            Example example,
            C c,
            Function<Model, Object> function,
            Integer count,
            Object queryId,
            Long pageNo
    ) {
        Objects.requireNonNull(dataSort);
        Objects.requireNonNull(direction);
        Objects.requireNonNull(readMapper);
        Objects.requireNonNull(example);
        Objects.requireNonNull(c);
        Objects.requireNonNull(function);
        if (count <= 0) {
            throw new PagingException("Param count must greater than 0.");
        }
        if (Objects.nonNull(pageNo)) {
            if (pageNo <= 0) {
                throw new PagingException("Param pageNo must greater than 0.");
            }
            if (pageNo > 1 && (Objects.isNull(queryId) || "".equals(queryId.toString().trim()))) {
                throw new PagingException("Param queryId must have value.");
            }
        }
        return PagingQuery.query(DataSort.AES.equals(dataSort), Direction.NEXT.equals(direction), queryTotal, readMapper, example, c, function, count, queryId, pageNo, null);
    }


    /**
     * 中轴分页
     *
     * @param dataSort     数据排序方式 AES or DESC
     * @param readMapper   Mapper
     * @param example      Example
     * @param c            Example.C
     * @param function     Example.C value function
     * @param count        Query count
     * @param nextId       Next Id value
     * @param pageNo       Page number . First page 1 , Last page -1
     * @param tagCount     Page tag count
     * @param <PrimaryKey>
     * @param <Model>
     * @param <Criteria>
     * @param <C>
     * @param <Example>
     * @return
     */
    public static <PrimaryKey, Model, Criteria extends AbstractGeneratedCriteria<?>, C extends Enum<C>, Example extends AbstractExample<Example, Criteria, C>> Page<Model> axisPage(
            DataSort dataSort,
            ReadMapper<PrimaryKey, Model, Example> readMapper,
            Example example,
            C c,
            Function<Model, Object> function,
            Integer count,
            Object nextId,
            Long pageNo,
            Integer tagCount
    ) {
        Objects.requireNonNull(dataSort);
        Objects.requireNonNull(readMapper);
        Objects.requireNonNull(example);
        Objects.requireNonNull(c);
        Objects.requireNonNull(function);
        Objects.requireNonNull(tagCount);
        if (count <= 0) {
            throw new PagingException("Param count must greater than 0.");
        }

        if (tagCount < 2 || (tagCount & 1) == 0) {
            throw new PagingException("Param tagCount must greater than or equal to 3 and odd.");
        }
        return PagingQuery.query(
                DataSort.AES.equals(dataSort),
                Objects.isNull(pageNo) || pageNo > 0,
                true,
                readMapper,
                example,
                c,
                function,
                count,
                Objects.isNull(pageNo) || pageNo <= 1 ? null : nextId,
                pageNo,
                tagCount);
    }

    private static class PagingQuery {
        /**
         * 分页查询 <br>
         *
         * @param dataAes      数据排序方式<br>
         *                     true 升序 , false 降序
         * @param pageNext     翻页动作<br>
         *                     true 向后翻页 , false 向前翻页
         * @param queryTotal   是否查询总数
         * @param readMapper   mapper
         * @param example      查询条件
         * @param c            查询条件字段
         * @param function     查询条件字段值获取 function
         * @param count        单次查询记录条数
         * @param queryId      向下或向上查询的ID 值 <br>
         *                     首页或尾页查询值可以为空。
         * @param pageNo       第N页  <br>
         *                     首页或尾页查询值可以为空。
         * @param tagCount     标记条数 <br>
         *                     不查询标记页值时设置值为空，标记页发布为奇数。
         *                     首页查询值可以为空。
         * @param <PrimaryKey>
         * @param <Model>
         * @param <Criteria>
         * @param <C>
         * @param <Example>
         * @return
         */
        private static <PrimaryKey, Model, Criteria extends AbstractGeneratedCriteria<?>, C extends Enum<C>, Example extends AbstractExample<Example, Criteria, C>> Page<Model> query(
                boolean dataAes,
                boolean pageNext,
                boolean queryTotal,
                ReadMapper<PrimaryKey, Model, Example> readMapper,
                Example example,
                C c,
                Function<Model, Object> function,
                Integer count,
                Object queryId,
                Long pageNo,
                Integer tagCount
        ) {
            CInterface cInterface = (CInterface) c;
            // 查询 Page （列表，总数，页数，是否有 上一页， 下一页）
            Page<Model> page = queryPage(dataAes, pageNext, queryTotal, readMapper, example, c, function, count, queryId, pageNo, cInterface);
            // Example 原查询列
            String columnList = example.dataGet(ExampleConstants.COLUMN_LIST);
            // 查询 tags
            queryAxis(dataAes, pageNext, readMapper, example, function, count, queryId, pageNo, tagCount, c, cInterface, page);
            // 清理 Example
            cleanExample(queryId, tagCount, example, columnList);
            return page;
        }

        /**
         * 查询 Page （列表，总数，页数，是否有 上一页， 下一页）
         *
         * @param dataAes
         * @param pageNext
         * @param queryTotal
         * @param readMapper
         * @param example
         * @param c
         * @param function
         * @param count
         * @param queryId
         * @param pageNo
         * @param cInterface
         * @param <PrimaryKey>
         * @param <Model>
         * @param <Criteria>
         * @param <C>
         * @param <Example>
         * @return
         */
        private static <PrimaryKey, Model, Criteria extends AbstractGeneratedCriteria<?>, C extends Enum<C>, Example extends AbstractExample<Example, Criteria, C>> Page<Model> queryPage(
                boolean dataAes,
                boolean pageNext,
                boolean queryTotal,
                ReadMapper<PrimaryKey, Model, Example> readMapper,
                Example example,
                C c,
                Function<Model, Object> function,
                Integer count,
                Object queryId,
                Long pageNo,
                CInterface cInterface) {
            Long total = null;
            // 查询总数
            if (queryTotal) {
                total = readMapper.countByExample(example);
            }
            // 存在ID
            boolean hasId = false;
            // 添加查询 where 条件 > 或 < 并放入第一位
            if (Objects.nonNull(queryId)) {
                hasId = true;
                Criteria criteriaInternal = example.createCriteriaInternal();
                criteriaInternal.ignoreNull();
                criteriaInternal.addCriterions(n -> {
                    String condition;
                    if (pageNext) {
                        condition = dataAes ? " > " : " < ";
                    } else {
                        condition = dataAes ? " < " : " > ";
                    }
                    n.addCriterion(cInterface.aliasDelimitedName() + condition, queryId, cInterface.getColumnName());
                });
                example.getOredCriteria().add(0, criteriaInternal);
            }
            // 设置查询排序
            if (pageNext) {
                if (dataAes) {
                    example.orderBy(c);
                } else {
                    example.orderByDesc(c);
                }
            } else {
                if (dataAes) {
                    example.orderByDesc(c);
                } else {
                    example.orderBy(c);
                }
            }
            long limit = count;
            if (!pageNext && Objects.isNull(queryId) && Objects.nonNull(total)) {
                long lastPageLimit = total % count;
                if (lastPageLimit > 0) {
                    limit = lastPageLimit;
                }
            }
            example.dataSet(ExampleConstants.LIMIT_START, 0L);
            // 多获取一条数据，用于判定是否有前一页或后一页
            example.dataSet(ExampleConstants.LIMIT_END, limit + 1);
            List<Model> list = readMapper.selectByExample(example);
            Page<Model> page = new Page<>();
            if (list.size() > 0) {
                if (!pageNext) {
                    // 反转List 排序
                    Collections.reverse(list);
                }
                boolean overflow = list.size() > limit;
                if (pageNext) {
                    if (overflow) {
                        list.remove(list.size() - 1);
                        page.setNextId(function.apply(list.get(list.size() - 1)));
                        page.setHasNext(true);
                    }
                    if (hasId) {
                        page.setPrevId(function.apply(list.get(0)));
                        page.setHasPrev(true);
                    }
                } else {
                    if (overflow) {
                        list.remove(0);
                        page.setPrevId(function.apply(list.get(0)));
                        page.setHasPrev(true);
                    }
                    if (hasId) {
                        page.setNextId(function.apply(list.get(list.size() - 1)));
                        page.setHasNext(true);
                    }
                }
            }

            if (!hasId) {
                if (pageNext) {
                    page.setFirst(true);
                    if (!page.getHasNext()) {
                        page.setLast(true);
                    }
                } else {
                    page.setLast(true);
                    if (!page.getHasPrev()) {
                        page.setFirst(true);
                    }
                }
            }

            if (queryTotal) {
                page.setTotal(total);
                page.setTotalPage(total > 0 ? (total % count > 0 ? total / count + 1 : total / count) : 0);
                if (page.getTotalPage() == 1 && (page.getHasPrev() || page.getHasNext())) {
                    page.setTotalPage(2L);
                }
            }


            if (pageNext && Objects.isNull(queryId)) {   // 首页
                page.setPageNo(1L);
            } else if (!pageNext && Objects.isNull(queryId)) {  // 尾页
                if (Objects.nonNull(page.getTotalPage()) && page.getTotalPage() > 0) {
                    page.setPageNo(page.getTotalPage());
                } else {
                    page.setPageNo(1L);
                }
            } else if (Objects.nonNull(pageNo)) {
                page.setPageNo(pageNo);
            }

            page.setData(list);
            page.setCurrent(Long.valueOf(list.size()));
            return page;
        }

        /**
         * 中轴标记
         *
         * @param dataAes
         * @param pageNext
         * @param readMapper
         * @param example
         * @param function
         * @param count
         * @param queryId
         * @param pageNo
         * @param tagCount
         * @param c
         * @param cInterface
         * @param page
         * @param <PrimaryKey>
         * @param <Model>
         * @param <Criteria>
         * @param <C>
         * @param <Example>
         */
        private static <PrimaryKey, Model, Criteria extends AbstractGeneratedCriteria<?>, C extends Enum<C>, Example extends AbstractExample<Example, Criteria, C>> void queryAxis(
                boolean dataAes,
                boolean pageNext,
                ReadMapper<PrimaryKey, Model, Example> readMapper,
                Example example,
                Function<Model, Object> function,
                Integer count,
                Object queryId,
                Long pageNo,
                Integer tagCount,
                C c,
                CInterface cInterface,
                Page<Model> page) {
            if (Objects.isNull(tagCount)) {
                return;
            }
            example.dataSet(ExampleConstants.COLUMN_LIST, cInterface.aliasDelimitedName());
            example.dataSet(ExampleConstants.LIMIT_START, 0L);
            example.dataSet(ExampleConstants.LIMIT_END, Long.valueOf(count * tagCount * 2));
            if (Objects.nonNull(queryId)) {
                example.getOredCriteria().remove(0);
            }
            List<Object> idList;
            if (Objects.nonNull(queryId)) {
                idList = new ArrayList<>();
                example.dataSet(ExampleConstants.LIMIT_END, Long.valueOf(count * tagCount));
                Criteria criteriaInternal = example.createCriteriaInternal();
                criteriaInternal.ignoreNull();

                // 生成向前的Ids-------- start
                criteriaInternal.addCriterions(n -> {
                    String condition = dataAes ? " < " : " > ";
                    n.addCriterion(cInterface.aliasDelimitedName() + condition, queryId, cInterface.getColumnName());
                });
                if (dataAes) {
                    example.orderByDesc(c);
                } else {
                    example.orderBy(c);
                }
                example.getOredCriteria().add(0, criteriaInternal);
                List<Object> prevList = readMapper.selectByExample(example).stream().map(function).collect(Collectors.toList());
                Collections.reverse(prevList);
                idList.addAll(prevList);
                // 生成向前的Ids-------- end

                // 生成向后的Ids-------- start
                criteriaInternal.getCriteria().clear();
                criteriaInternal.addCriterions(n -> {
                    String condition = dataAes ? " >= " : " <= ";
                    n.addCriterion(cInterface.aliasDelimitedName() + condition, queryId, cInterface.getColumnName());
                });
                if (dataAes) {
                    example.orderBy(c);
                } else {
                    example.orderByDesc(c);
                }


                List<Object> nextList = readMapper.selectByExample(example).stream().map(function).collect(Collectors.toList());
                idList.addAll(nextList);
                // 生成向前的Ids-------- end

            } else {
                idList = readMapper.selectByExample(example).stream().map(function).collect(Collectors.toList());
            }
            if (!pageNext) {
                // 从尾页查找
                Collections.reverse(idList);
            }

            List<PageTag> tags = new ArrayList<>();
            int currentDataIndex = -1;
            // 确定中轴位置
            if (page.getFirst()) {
                PageTag pageTag = new PageTag();
                pageTag.setActive(true);
                pageTag.setPageNo(1);
                tags.add(pageTag);
            } else {
                for (int i = 0; i < idList.size(); i++) {
                    if (idList.get(i).equals(page.getPrevId())) {
                        currentDataIndex = i - 1;
                        PageTag pageTag = new PageTag();
                        pageTag.setActive(true);
                        pageTag.setNextId(idList.get(currentDataIndex));
                        pageTag.setPageNo(Objects.isNull(pageNo) || pageNo <= 0 ? page.getTotalPage() : pageNo);
                        tags.add(pageTag);
                        break;
                    }
                }
            }
            // 中轴左侧填充tag
            if (!page.getFirst()) {
                for (int i = currentDataIndex - count; i >= 0; i -= count) {
                    PageTag pageTag = new PageTag();
                    pageTag.setNextId(idList.get(i));
                    pageTag.setPageNo(tags.get(0).getPageNo() - 1);
                    tags.add(0, pageTag);
                }
            }
            // 中轴右侧填充tag
            if (!page.getLast()) {
                for (int i = currentDataIndex + count; i < idList.size(); i += count) {
                    PageTag pageTag = new PageTag();
                    pageTag.setNextId(idList.get(i));
                    pageTag.setPageNo(tags.get(tags.size() - 1).getPageNo() + 1);
                    tags.add(pageTag);
                }
            }
            // 中轴填充首页
            if (tags.size() > 0 && tags.get(0).getPageNo() == 2) {
                PageTag pageTag = new PageTag();
                pageTag.setPageNo(1);
                tags.add(0, pageTag);
            }
            int currentTagIndex = 0;
            // 获取当前页索引
            for (int i = 0; i < tags.size(); i++) {
                if (tags.get(i).isActive()) {
                    currentTagIndex = i;
                }
            }

            // 设置上一页id
            page.setPrevId(null);
            if (currentTagIndex - 1 >= 0) {
                page.setPrevId(tags.get(currentTagIndex - 1).getNextId());
            }
            // 设置下一页id
            page.setNextId(null);
            if (currentTagIndex + 1 < tags.size()) {
                page.setNextId(tags.get(currentTagIndex + 1).getNextId());
            }

            // 截取tag
            if (tags.size() > tagCount) {
                List<PageTag> temp = new ArrayList<>();
                temp.add(tags.get(currentTagIndex));
                for (int i = 1; i <= tagCount; i++) {
                    int left = currentTagIndex - i;
                    if (left >= 0) {
                        temp.add(0, tags.get(left));
                        if (temp.size() == tagCount) {
                            break;
                        }
                    }
                    int right = currentTagIndex + i;
                    if (right < tags.size()) {
                        temp.add(tags.get(right));
                        if (temp.size() == tagCount) {
                            break;
                        }
                    }
                }
                page.setTags(temp);
            } else {
                page.setTags(tags);
            }
        }

        /**
         * 清理Example
         *
         * @param queryId
         * @param tagCount
         * @param example
         * @param columnList
         * @param <Criteria>
         * @param <C>
         * @param <Example>
         */
        private static <Criteria extends AbstractGeneratedCriteria<?>, C extends Enum<C>, Example extends AbstractExample<Example, Criteria, C>> void cleanExample(
                Object queryId,
                Integer tagCount,
                Example example,
                String columnList) {
            /*if (Objects.nonNull(queryId) || Objects.nonNull(tagCount)) {
                example.getOredCriteria().remove(0);
            }*/
            example.setOrderByClause(null);
            example.dataSet(ExampleConstants.LIMIT_START, null);
            example.dataSet(ExampleConstants.LIMIT_END, null);
            example.dataSet(ExampleConstants.COLUMN_LIST, columnList);
        }
    }

}
