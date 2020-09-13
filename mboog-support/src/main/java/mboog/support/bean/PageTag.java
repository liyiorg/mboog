package mboog.support.bean;

import java.io.Serializable;

/**
 * @author LiYi
 */
public class PageTag implements Serializable {


    private static final long serialVersionUID = 528271809255975492L;

    private long pageNo;

    private Object nextId;

    private boolean active;

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public Object getNextId() {
        return nextId;
    }

    public void setNextId(Object nextId) {
        this.nextId = nextId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
