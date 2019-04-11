package mboog.support.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author LiYi 2010-09-26
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -5562537683939089397L;

    // 当前页码
    private long pageNo;

    // 每页展现多少条记录
    private long pageSize;

    // 总记录条数
    private long total;

    // 当前记录条数
    private long current;

    // 总页数
    private long totalPage;

    // 承载数据
    private List<T> data;

    // 是否有上一页
    private boolean hasPrev;

    // 是否有下一页
    private boolean hasNext;

    // 是否为第一页
    private boolean first;

    // 是否为最后一页
    private boolean last;

    // 下一条数记录的id
    private String nextId;

    public Page(long pageNo, long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Page(List<T> data, long totalRecord, long page, long pageSize) {
        this.pageNo = page;
        if (page < 0)
            this.pageNo = 1;
        this.pageSize = pageSize;
        if (pageSize < 0)
            this.pageSize = 1;
        this.current = data.size();
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
        if (pageNo == totalPage) {
            this.last = true;
        }
    }

    public long getPageNo() {
        return pageNo;
    }

    public long getPageSize() {
        return pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public boolean getHasPrev() {
        return hasPrev;
    }

    public boolean getHasNext() {
        return hasNext;
    }

    public boolean getFirst() {
        return first;
    }

    public boolean getLast() {
        return last;
    }

    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }
}
