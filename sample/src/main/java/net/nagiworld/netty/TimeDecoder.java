package net.nagiworld.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Created by IntelliJ IDEA.
 * User: nagesh
 * Date: Nov 21, 2010
 * Time: 4:45:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class TimeDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) {

        if (buffer.readableBytes() < 4) {
            return null;
        }

        return buffer.readBytes(4);
    }
}