package net.nagiworld.netty.dispatch;

import net.nagiworld.netty.HttpClientSample;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: nageshs
 * Date: Nov 23, 2010
 * Time: 9:59:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Dispatcher {

  public static void dispatch(final HttpRequest request, final MessageEvent me) throws Exception {
    // Use supreme logic to determine the actual chain of handlers for this request
    // For simplicity here lets assume it boils down to a single
    // handler
    System.out.println("Thread in dispatcher " + Thread.currentThread() + " req " + request.hashCode());
    if ("/foo".equals(request.getUri())) {
      HttpClientSample s = new HttpClientSample(new URI("http://www.yahoo.com/"));
      s.connect(new SimpleChannelUpstreamHandler() {
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
          System.out.println("got message from remote server " + Thread.currentThread() + " req " + request.hashCode());
          HttpResponse resp = (HttpResponse) e.getMessage();
//          HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
//          response.setContent(resp.getContent());
//          response.setHeader("Connection", "close");
//          response.setHeader("Server", "nws");
          ChannelFuture f = me.getChannel().write(resp);
          f.addListener(ChannelFutureListener.CLOSE);
          System.out.println("done");
        }

        public void exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext channelHandlerContext, org.jboss.netty.channel.ExceptionEvent exceptionEvent) throws java.lang.Exception {
          exceptionEvent.getCause().printStackTrace();
          me.getChannel().close();
        }
      });

    }
    System.out.println("done requesting from dispatch");
  }

}
