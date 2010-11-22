package net.nagiworld.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

/**
 * Created by IntelliJ IDEA.
 * User: nagesh
 * Date: Nov 20, 2010
 * Time: 11:12:01 PM
 */
public class SampleServerHandler extends SimpleChannelHandler {

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        System.out.println("channelConnected");
        Channel ch = e.getChannel();
        ChannelBuffer buffer = ChannelBuffers.buffer(4);
        buffer.writeInt((int)(System.currentTimeMillis()/1000));

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
