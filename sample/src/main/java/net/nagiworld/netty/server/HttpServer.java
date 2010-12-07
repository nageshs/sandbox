package net.nagiworld.netty.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: nagesh
 * Date: Nov 21, 2010
 * Time: 5:26:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpServer {

    public static void main(String[] args) {
        ChannelFactory factory = new NioServerSocketChannelFactory(
                //Executors.newCachedThreadPool(),
                //Executors.newCachedThreadPool()
                Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(5),
                5 /* want only 5 rps */
        );

        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() throws Exception {
              return Channels.pipeline(
                        new HttpRequestDecoder(),
                      new HttpChunkAggregator(10485760),
                      new HttpResponseEncoder(),
                      new ChunkedWriteHandler(),
                      //new HttpServerHandler()
                      new HttpCustomHandler()
              );
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        bootstrap.bind(new InetSocketAddress(8080));
        System.out.println("Listening on port 8080");
        
    }
}
