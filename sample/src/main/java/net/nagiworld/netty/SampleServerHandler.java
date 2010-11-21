package net.nagiworld.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

/**
 * Created by IntelliJ IDEA.
 * User: nagesh
 * Date: Nov 20, 2010
 * Time: 11:12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleServerHandler extends SimpleChannelHandler {

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        System.out.println("messageReceived");
//        Channel ch  = e.getChannel();
//        ch.write(e.getMessage());
        Channel ch = e.getChannel();
        ChannelBuffer buffer = ChannelBuffers.buffer(8);
        buffer.writeLong((System.currentTimeMillis()/1000));

        ChannelFuture future = ch.write(buffer);
        future.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture future) throws Exception {
                future.getChannel().close();
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        Channel ch = e.getChannel();
        ch.close();
    }

}
