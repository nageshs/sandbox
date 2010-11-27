package net.nagiworld.netty;

import net.nagiworld.netty.dispatch.Dispatcher;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * Created by IntelliJ IDEA.
 * User: nagesh
 * Date: Nov 23, 2010
 * Time: 9:53:11 PM
 */
public class HttpCustomHandler extends SimpleChannelHandler {

  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
    // By now we've got a HttpRequest so all thats needed is to dispatch it
    HttpRequest request = (HttpRequest)e.getMessage();
    Dispatcher.dispatch(request, e);

  }
}
