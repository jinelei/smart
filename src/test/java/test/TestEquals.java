package test;

import org.junit.Assert;
import org.junit.Test;

import cn.jinelei.rainbow.smart.Enums;
import cn.jinelei.rainbow.smart.Enums.Category;
import cn.jinelei.rainbow.smart.Enums.DevStatusTag;

public class TestEquals {

    @Test
    public void testString() {
        Assert.assertTrue(Enums.equals(Enums.Strings.SERVER_ADDR, "00:00:00:00:00:00"));
    }

    @Test
    public void testEnum() {
        Assert.assertTrue(Enums.equals(Enums.Strings.SERVER_ADDR,
                Enums.valueOf(Enums.Strings.SERVER_ADDR, "00:00:00:00:00:00")));
    }

    @Test
    public void testCategory() {
        Assert.assertTrue(Enums.equals(Enums.Category.DEV_STATUS, 0x01));
    }

    @Test
    public void testEquals1(){
        Assert.assertTrue(Enums.equals(DevStatusTag.DEV_HEARTBEAT, 0x03));
        Assert.assertTrue(Enums.equals(Category.DEV_STATUS, 0x01));
    }
}