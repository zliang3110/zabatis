package reflection.property;

import java.util.Iterator;

public class PropertyTokenizer implements Iterator<PropertyTokenizer> {
    private String name;
    private final String indexedName;
    private String index;
    private final String childen;

    /**
     *
     * 解析 a[1] 形式，解析完后name = a,indexName = a, index = 1
     * 如果是a[1].b[2] 形式，可以通过调用next方法解析b[2]
     * @param fullname
     */
    public PropertyTokenizer(String fullname)
    {
        int delim = fullname.indexOf('.');
        if (delim > -1)
        {
            name = fullname.substring(0, delim);
            childen = fullname.substring(delim+1);
        }else {
            name = fullname;
            childen = null;
        }

        indexedName = name;
        delim = name.indexOf('[');
        if (delim > -1){
            index = name.substring(delim + 1, name.length() - 1);
            name = name.substring(0, delim);
        }
    }

    public String getName() {
        return name;
    }

    public String getIndexedName() {
        return indexedName;
    }

    public String getIndex() {
        return index;
    }

    public String getChilden() {
        return childen;
    }

    @Override
    public boolean hasNext() {
        return childen != null;
    }

    @Override
    public PropertyTokenizer next() {
        return new PropertyTokenizer(childen);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("不支持删除操作.");
    }
}
