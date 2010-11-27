package net.nagiworld.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: nageshs
 * Date: Nov 24, 2010
 * Time: 9:49:18 PM
 */
public class HttpClientSample {

  private static ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
          Executors.newCachedThreadPool());
  
  private URI uri;

  public HttpClientSample(URI uri) {
    this.uri = uri;
  }

  public void connect(final SimpleChannelUpstreamHandler handler) {
    final String host = uri.getHost();
    int _port = uri.getPort();
    if (_port == -1) _port = 80;
    final int port = _port;
    
    final ClientBootstrap bootstrap = new ClientBootstrap(factory);
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

      public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(
                new HttpClientCodec(),
                new HttpChunkAggregator(10485760),
                new HttpContentDecompressor(),
                handler);
      }
    });

    bootstrap.setOption("tcpNoDelay", true);
    bootstrap.setOption("keepAlive", true);

// Start the connection attempt.
    ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

    future.addListener(new ChannelFutureListener() {

      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        // Wait until the connection attempt succeeds or fails.
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
          future.getCause().printStackTrace();
          bootstrap.releaseExternalResources();
          return;
        }

        // Prepare the HTTP request.
        HttpRequest request = new DefaultHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
        request.setHeader(HttpHeaders.Names.HOST, host);
        request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
//    request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

        // Set some example cookies.
//    CookieEncoder httpCookieEncoder = new CookieEncoder(false);
//    httpCookieEncoder.addCookie("my-cookie", "foo");
//    httpCookieEncoder.addCookie("another-cookie", "bar");
//    request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());

        // Send the HTTP request.
        channel.write(request);
        System.out.println("wrote req");
        // Wait for the server to close the connection.
        //channel.getCloseFuture().awaitUninterruptibly();
        
      }
    });
  }

  public static void main(String[] args) throws URISyntaxException {
    HttpClientSample s = new HttpClientSample(new URI("http://www.yahoo.com/"));
    s.connect(new SimpleChannelUpstreamHandler() {
      public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        System.out.println("got message from remote server ");
        HttpResponse resp = (HttpResponse)e.getMessage();
        System.out.println(resp.getStatus() + " resp " + resp);
        System.out.println(resp.getContent().toString());
        //e.getChannel().close();
        System.out.println("done");
      }

    });

  }
}
