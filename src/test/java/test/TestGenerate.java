package test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import cn.jinelei.rainbow.smart.model.JySmartProto;

public class TestGenerate {

    @Test
    public void testJySmartProto() throws IOException {
        byte version = 1;
        byte crc = 0x01;
        byte[] srcAddr = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06};
        byte[] dstAddr = new byte[]{0x06,0x05,0x04,0x03,0x02,0x01};
        long timestamp = 0L;
        byte seq = 1;
        byte category = 2;
        byte tag = 3;
        short last = 3;
        short length = 0;
        byte[] data = new byte[0];
        JySmartProto proto = new JySmartProto(version, crc, srcAddr, dstAddr, timestamp, seq, category, tag, last, length, data);
        String url = this.getClass().getResource("/").getPath() + (String.format("/proto-%s.bin", new SimpleDateFormat("MMddhhmm").format(new Date())));
        RandomAccessFile aFile = new RandomAccessFile(url, "rw");
        aFile.seek(0);
        aFile.write(proto.getBytes());
        aFile.close();
    }
}