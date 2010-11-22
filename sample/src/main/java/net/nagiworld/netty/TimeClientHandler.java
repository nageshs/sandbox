package net.nagiworld.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: nagesh
 * Date: Nov 21, 2010
 * Time: 1:40:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class TimeClientHandler extends SimpleChannelHandler {
    private final ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        ChannelBuffer m = (ChannelBuffer)e.getMessage();
        buf.writeBytes(m);

        if (buf.readableBytes() >= 4) {
            System.out.println("reading date ");
            long currentTimeMillis = buf.readInt() * 1000L;
            System.out.println(new Date(currentTimeMillis));
            e.getChannel().close();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
