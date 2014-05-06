package com.hillstone.hsa.index.utils;

/**
 * Created with IntelliJ IDEA.
 * User: hillstone
 * Date: 13-5-17
 * Time: 下午4:37
 * 查询分页
 */
import com.hillstone.hsa.index.domain.LogObj;

import java.util.List;

public class QueryResult {
    private int count;
    private List<LogObj> list;

    public QueryResult(int count, List list) {
        this.count = count;
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<LogObj> getList() {
        return list;
    }

    public void setList(List<LogObj> list) {
        this.list = list;
    }
}
