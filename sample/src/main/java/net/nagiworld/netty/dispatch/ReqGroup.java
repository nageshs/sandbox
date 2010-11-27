package net.nagiworld.netty.dispatch;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;

/**
   * Operation mashup
 * need to send a request to resources 1, 2, 3, 4, 5, asynchronously
 * when* all are done i need it to call method X
 * - Have an object called Group that keep tracks of responses for each event
 * - Each response when received updates the Group with response and also removes itself
 * - When removing itself it checks if its the last guy and if so calls another method which
 * performs the remaining actions
 */

public class ReqGroup<E> {
  //ChannelGroup group
  private AtomicInteger done;
  private Map<String, E> map;
  private ReqGroupHandler handler;

  public ReqGroup(int total, ReqGroupHandler handler) {
    this.done = new AtomicInteger(total);
    this.map = new HashMap<String, E>();
    this.handler = handler;
  }

  // todo what if the same key is put in twice??
  public void addResponse(String key, E value) {
    map.put(key, value);
    if (done.decrementAndGet() <=0) { // Need another case which is || timeHasElapsed
      // to address the case where the time of X seconds has elapsed and not all entries have
      // responded.
      handler.mergeEvent(this);
    }
  }

  public Map<String,E> getMap() {
    return map;
  }
}
