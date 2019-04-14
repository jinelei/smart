package test;

import org.junit.Test;
import protobuf.Message;

public class TestPkt {
    @Test
    public void test(){
        Message.Pkt pkt = Message.Pkt.newBuilder()
                .setDstAddr("")
                .build();
    }
}
