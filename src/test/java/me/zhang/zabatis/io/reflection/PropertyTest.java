package me.zhang.zabatis.io.reflection;

import me.zhang.zabatis.io.pojo.Human;
import org.junit.Test;
import reflection.property.PropertyCopier;

public class PropertyTest {

    @Test
    public void propertyCopierTest()
    {
        Human var1 = new Human("zhangsan", 1.70, 1
                , new Human("zhanger", 1.70, 0, null));

        Human var2 = new Human();
        PropertyCopier.copyBeanProperties(Human.class, var1, var2);

        assert var2.getChild() == var1.getChild();
    }
}
