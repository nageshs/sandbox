package net.nagiworld.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;

/**
 * Created by IntelliJ IDEA.
 * User: nagesh
 * Date: Nov 21, 2010
 * Time: 5:07:10 PM
 */
public class HttpServerHandler extends SimpleChannelHandler {
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object o = e.getMessage();
        HttpRequest req = (HttpRequest)o;
        HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //res.addHeader("Connection", "close");
        final ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
        buf.writeBytes("this is the response".getBytes("ASCII"));
        //System.out.println(res.getContent());
        res.setContent(buf);
        res.addHeader("Connection", "keep-alive");
        //res.addHeader("Content-Length", "this is the response".length());

        ChannelFuture future = e.getChannel().write(res);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        Channel ch = e.getChannel();
        ch.close();
    }

    /* check out
     http://grepcode.com/file/repository.jboss.org/nexus/content/repositories/releases/org.jboss.netty/netty/3.2.0.CR1/org/jboss/netty/example/http/snoop/HttpRequestHandler.java#HttpRequestHandler
      */
}
