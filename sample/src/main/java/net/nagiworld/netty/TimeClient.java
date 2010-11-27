package net.nagiworld.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: nagesh
 * Date: Nov 21, 2010
 * Time: 2:00:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TimeClient {

    public static void doit() {
        String host = "localhost";
        int port = 8080;
        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        ClientBootstrap bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                        new TimeDecoder(), 
                        new TimeClientHandler());
            }
        });

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        bootstrap.connect(new InetSocketAddress(host, port));
    }

  public static void main(String[] args) {
    doit();
  }
}
