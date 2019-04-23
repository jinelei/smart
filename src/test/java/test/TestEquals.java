package test;

import cn.jinelei.rainbow.smart.model.enums.Category;
import cn.jinelei.rainbow.smart.model.enums.DevStatusTag;
import cn.jinelei.rainbow.smart.model.enums.EnumHelper;
import cn.jinelei.rainbow.smart.model.enums.Strings;
import org.junit.Assert;
import org.junit.Test;


public class TestEquals {

    @Test
    public void testString() {
        Assert.assertTrue(EnumHelper.equals(Strings.SERVER_ADDR, "00:00:00:00:00:00"));
    }

    @Test
    public void testEnum() {
        Assert.assertTrue(EnumHelper.equals(Strings.SERVER_ADDR,
                EnumHelper.valueOf(Strings.SERVER_ADDR, "00:00:00:00:00:00")));
    }

    @Test
    public void testCategory() {
        Assert.assertTrue(EnumHelper.equals(Category.DEV_STATUS, 0x01));
    }

    @Test
    public void testEquals1(){
        Assert.assertTrue(EnumHelper.equals(DevStatusTag.DEV_HEARTBEAT, 0x03));
        Assert.assertTrue(EnumHelper.equals(Category.DEV_STATUS, 0x01));
    }
}