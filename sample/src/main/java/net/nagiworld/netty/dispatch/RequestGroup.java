package net.nagiworld.netty.dispatch;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
   * Operation mashup
 * need to send a request to resources 1, 2, 3, 4, 5, asynchronously
 * when* all are done i need it to call method X
 * - Have an object called Group that keep tracks of responses for each event
 * - Each response when received updates the Group with response and also removes itself
 * - When removing itself it checks if its the last guy and if so calls another method which
 * performs the remaining actions
 */
//todo what do we do when we want to indicate a timeout?
  
public class RequestGroup<E> implements ChannelFutureListener {
  //ChannelGroup group
  private AtomicInteger done;
  private ConcurrentMap<String, E> map;
  private RequestGroupHandler handler;

  /**
   * @param total number of requests you wish to complete
   * @param handler RegGroupHandler that should be called oncompletion
   */
  public RequestGroup(int total, RequestGroupHandler handler) {
    this.done = new AtomicInteger(total);
    this.map = new ConcurrentHashMap<String, E>();
    this.handler = handler;
  }

  // todo what if the same key is put in twice?? really shoudlnt care
  // about this since this is what the user chose
  public void addResponse(String key, E value, Channel channel) {
    channel.getCloseFuture().addListener(this);
    map.putIfAbsent(key, value);
  }

  public ConcurrentMap<String,E> getMap() {
    return map;
  }

  @Override
  public void operationComplete(ChannelFuture channelFuture) throws Exception {
    System.out.println("ID: " + channelFuture.getChannel().getId());
    /**
     * review: Use channelgroup.size() to determine if its really done.
     * Buf the question remains? What is there is a missed call?
     */
    if (done.decrementAndGet() <= 0) {
      handler.mergeEvent(this);
    }
  }
}
