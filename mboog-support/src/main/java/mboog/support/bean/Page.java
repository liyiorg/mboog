package mboog.support.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author LiYi 2010-09-26
 */
public class Page<T> extends LitePage<T> implements Serializable {

    private static final long serialVersionUID = -5562537683939089397L;

    /**
     * 当前页码
     */
    private Long pageNo;

    /**
     * 每页展现多少条记录
     */
    private Long pageSize;

    /**
     * 总记录条数
     */
    private Long total;

    /**
     * 当前记录条数
     */
    private Long current;

    /**
     * 总页数
     */
    private Long totalPage;

    /**
     * 是否有上一页
     */
    private Boolean hasPrev;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否为第一页
     */
    private Boolean first;

    /**
     * 是否为最后一页
     */
    private Boolean last;

    private List<PageTag> tags;

    public Page() {
        hasNext = false;
        hasPrev = false;
        first = false;
        last = false;
        current = 0L;
    }

    public Page(long pageNo, long pageSize) {
        this();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Page(List<T> data, long totalRecord, long page, long pageSize) {
        this();
        this.pageNo = page;
        if (page < 0) {
            this.pageNo = 1L;
        }
        this.pageSize = pageSize;
        if (pageSize < 0) {
            this.pageSize = 1L;
        }
        this.current = Long.valueOf(data.size());
        this.data = data;
        this.total = totalRecord;
        long totalPageNum = this.total / this.pageSize;
        this.totalPage = (this.total % this.pageSize > 0) ? totalPageNum + 1 : totalPageNum;
        if (pageNo > totalPage) {
            pageNo = totalPage;
        }
        if (pageNo > 1) {
            this.hasPrev = true;
        }
        if (pageNo < this.totalPage) {
            this.hasNext = true;
        }
        if (pageNo == 1) {
            this.first = true;
        }
        if (pageNo.equals(totalPage)) {
            this.last = true;
        }
    }

    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public List<T> getData() {
        return data;
    }

    @Override
    public void setData(List<T> data) {
        this.data = data;
    }

    public Boolean getHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(Boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public List<PageTag> getTags() {
        return tags;
    }

    public void setTags(List<PageTag> tags) {
        this.tags = tags;
    }
}
