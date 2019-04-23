package test.unit;

import cn.jinelei.rainbow.smart.model.enums.*;
import cn.jinelei.rainbow.smart.helper.PktHelper;
import org.junit.Assert;
import org.junit.Test;

public class TestEnum {

    @Test
    public void testEnumEquals() {
        // Category
        Assert.assertTrue(PktHelper.enumEquals(Category.RESERVED, 0x00));
        Assert.assertTrue(PktHelper.enumEquals(Category.DEV_STATUS, 0x01));
        Assert.assertTrue(PktHelper.enumEquals(Category.DEV_OPERATE, 0x02));

        // DevFeature
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.RESERVED, 0x00));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.LIGHT_SENSOR, 0x01));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.HUMAN_SENSOR, 0x02));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.MICROPHONE, 0x04));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.SPEAKER, 0x08));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.ELETRIC_RELAY, 0x10));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.WS2812, 0x20));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.TEMPERATURE_SENSOR, 0x40));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.HUMIDITY_SENSOR, 0x80));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.RAINFALL_SENSOR, 0x100));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.CAMERA, 0x200));
        Assert.assertTrue(PktHelper.enumEquals(DevFeature.LIGHT, 0x400));

        // DevOperateTag
        Assert.assertTrue(PktHelper.enumEquals(DevOperateTag.RESERVED, 0x00));
        Assert.assertTrue(PktHelper.enumEquals(DevOperateTag.DEV_OPERATE_QUERY_ONLINE, 0x01));
        Assert.assertTrue(PktHelper.enumEquals(DevOperateTag.DEV_OPERATE_QUERY_SUDDEN_DEATH, 0x02));
        Assert.assertTrue(PktHelper.enumEquals(DevOperateTag.DEV_OPERATE_QUERY_DEAD, 0x03));

        // DevStatusTag
        Assert.assertTrue(PktHelper.enumEquals(DevStatusTag.RESERVED, 0x00));
        Assert.assertTrue(PktHelper.enumEquals(DevStatusTag.DEV_LOGIN, 0x01));
        Assert.assertTrue(PktHelper.enumEquals(DevStatusTag.DEV_LOGOUT, 0x02));
        Assert.assertTrue(PktHelper.enumEquals(DevStatusTag.DEV_HEARTBEAT, 0x03));

        // ErrCode
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.SUCCESS, 0x00));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.FAILURE, 0x01));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.INVALID_PARAM, 0x02));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.NOT_IMPLEMENTED, 0x03));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.DEPRECATED, 0x04));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.NO_PERMISSION, 0x05));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.EAUTH, 0x06));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.NOT_ALLOWED, 0x07));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.NOT_EXISTS, 0x08));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.ALREADY_EXISTS, 0x09));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.NO_DEVICE, 0x0A));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.USER_OR_PASSWD_WRONG, 0x0B));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.OFFLINE, 0x0C));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.ALREADY_CANCELED, 0x0D));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.IN_PROGRESS, 0x0E));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.EXPIRED, 0x0F));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.ALREADY_FINISH, 0x10));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.IS_EMPTY, 0x11));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.INVALIID_FULLD_PARAM, 0x12));
        Assert.assertTrue(PktHelper.enumEquals(ErrCode.DATA_CONFLICT, 0x13));
    }

}
