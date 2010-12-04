package net.nagiworld.netty.dispatch;

import net.nagiworld.netty.HttpClient;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    final RequestGroup<HttpResponse> group = new RequestGroup<HttpResponse>(2, new RequestGroupHandler() {

      @Override
      public void mergeEvent(RequestGroup group) {
        System.out.println("mergeevent called ");
        Collection<HttpResponse> resps = group.getMap().values();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setHeader("Connection", "close");
        response.setHeader("Server", "nws");
        //response.setChunked(true);
        List<ChannelBuffer> bufs = new ArrayList<ChannelBuffer>(resps.size());
        long contentLength = 0l;
        for (HttpResponse resp: resps) {
          if (resp.getContent().capacity() > 0) {
            bufs.add(resp.getContent());
            contentLength += resp.getContent().writerIndex();
          }
          System.out.println("adding : " + resp.getContent());

        }
        System.out.println("bufs " + bufs);
        response.setHeader("Content-Length", contentLength);
        response.setContent(ChannelBuffers.copiedBuffer(bufs.toArray(new ChannelBuffer[bufs.size()])));

        System.out.println("response " + response);
        me.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
      }
    });

    final String s1 = "http://www.yahoo.com/";
    HttpClient s = new HttpClient(new URI(s1));

    
    s.connect(new SimpleChannelUpstreamHandler() {

      public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        System.out.println("got message from remote server " + Thread.currentThread() + " req " + request.hashCode());
        HttpResponse resp = (HttpResponse) e.getMessage();
        //ChannelFuture f = me.getChannel().write(resp);
        System.out.println("got content: " + resp.getContent());
        //f.addListener(ChannelFutureListener.CLOSE);
        group.addResponse(s1, resp, e.getChannel());
        System.out.println("done");
        e.getChannel().close();
      }

      public void exceptionCaught(ChannelHandlerContext channelHandlerContext, ExceptionEvent ex) throws java.lang.Exception {
        ex.getCause().printStackTrace();
        ex.getChannel().close();
      }

    });


    final String s2 = "http://www.cnn.com/";
    HttpClient sample = new HttpClient(new URI(s2));


    sample.connect(new SimpleChannelUpstreamHandler() {

      public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        System.out.println("got message from remote server " + Thread.currentThread() + " req " + request.hashCode());
        HttpResponse resp = (HttpResponse) e.getMessage();
        //ChannelFuture f = me.getChannel().write(resp);
        //f.addListener(ChannelFutureListener.CLOSE);
        System.out.println("got content: " + resp.getContent());
        group.addResponse(s2, resp, e.getChannel());
        System.out.println("done");
        e.getChannel().close();
      }

      public void exceptionCaught(ChannelHandlerContext channelHandlerContext, ExceptionEvent ex) throws java.lang.Exception {
        ex.getCause().printStackTrace();
        ex.getChannel().close();
      }

    });

    System.out.println("done requesting from dispatch");
  }

}
