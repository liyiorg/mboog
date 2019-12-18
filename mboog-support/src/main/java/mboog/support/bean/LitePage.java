package mboog.support.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author LiYi
 */
public class LitePage<T> implements Serializable {

    private static final long serialVersionUID = 8020924493889392350L;

    // 承载数据
    protected List<T> data;

    // 上一页数记录的id
    protected Object prevId;

    // 下一页数记录的id
    protected Object nextId;


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Object getPrevId() {
        return prevId;
    }

    public void setPrevId(Object prevId) {
        this.prevId = prevId;
    }

    public Object getNextId() {
        return nextId;
    }

    public void setNextId(Object nextId) {
        this.nextId = nextId;
    }
}
