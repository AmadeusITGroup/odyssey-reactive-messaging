package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMessageContext;

public class MessageImplTest {

  private static class AContext implements MessageContext {
    public static String KEY = "A";

    private String payload;

    public AContext(String payload) {
      this.payload = payload;
    }

    @Override
    public String getContextKey() {
      return KEY;
    }

    @Override
    public boolean isPropagable() {
      return true;
    }

    @Override
    public String getContextMergeKey() {
      return KEY;
    }

    @Override
    public MessageContext merge(MessageContext... messageContexts) {
      MultiAContext multiAContext = new MultiAContext(this);
      multiAContext.merge(messageContexts);
      return multiAContext;
    }

    public String getPayload() {
      return payload;
    }
  }

  private static class MultiAContext implements MutableMessageContext {
    public static String KEY = "MA";

    private List<AContext> aContexts = new ArrayList<>();

    public MultiAContext() {
    }

    public MultiAContext(MultiAContext multiAContext) {
      aContexts.addAll(multiAContext.getAContexts());
    }

    public MultiAContext(AContext aContext) {
      aContexts.add(aContext);
    }

    @Override
    public String getContextKey() {
      return KEY;
    }

    @Override
    public boolean isPropagable() {
      return true;
    }

    @Override
    public String getContextMergeKey() {
      return AContext.KEY;
    }

    public List<AContext> getAContexts() {
      return new ArrayList<>(aContexts);
    }

    public void add(AContext aContext) {
      aContexts.add(aContext);
    }

    @Override
    public MessageContext merge(MessageContext... messageContexts) {
      for (MessageContext mc : messageContexts) {
        if (mc instanceof AContext) {
          add((AContext) mc);
        } else {
          aContexts.addAll(((MultiAContext) mc).getAContexts());
        }
      }
      return this;
    }

    @Override
    public MutableMessageContext createChild() {
      return new MultiAContext(this);
    }
  }

  private static class BContext implements MessageContext {
    public static String KEY = "B";

    @Override
    public String getContextKey() {
      return KEY;
    }

    @Override
    public boolean isPropagable() {
      return true;
    }

    @Override
    public String getContextMergeKey() {
      return KEY;
    }

    @Override
    public MessageContext merge(MessageContext... messageContexts) {
      throw new RuntimeException("not implemented");
    }
  }

  @Test
  public void mergeContext() {
    AContext a1 = new AContext("1");
    Message<String> message = Message.<String> builder()
        .addContext(a1)
        .addContext(new BContext())
        .build();

    Assert.assertTrue(message.hasContext(AContext.KEY));
    Assert.assertFalse(message.hasContext(MultiAContext.KEY));
    Assert.assertTrue(message.hasMergeableContext(AContext.KEY));
    Assert.assertTrue(message.hasContext(BContext.KEY));

    AContext a2 = new AContext("2");
    message.addContext(a2);
    Assert.assertFalse(message.hasContext(AContext.KEY));
    Assert.assertTrue(message.hasContext(MultiAContext.KEY));
    Assert.assertTrue(message.hasMergeableContext(AContext.KEY));
    Assert.assertTrue(message.hasContext(BContext.KEY));

    MultiAContext multiAContext = message.getContext(MultiAContext.KEY);
    Assert.assertEquals(2, multiAContext.getAContexts()
        .size());
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a1));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a2));

    AContext a3 = new AContext("3");
    multiAContext = new MultiAContext(a3);
    message.addContext(multiAContext);
    Assert.assertFalse(message.hasContext(AContext.KEY));
    Assert.assertTrue(message.hasContext(MultiAContext.KEY));
    Assert.assertTrue(message.hasMergeableContext(AContext.KEY));
    Assert.assertTrue(message.hasContext(BContext.KEY));

    multiAContext = message.getContext(MultiAContext.KEY);
    Assert.assertEquals(3, multiAContext.getAContexts()
        .size());
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a1));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a2));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a3));

    Message<String> childMessage = Message.<String> builder()
        .fromParents(message)
        .build();
    Assert.assertFalse(childMessage.hasContext(AContext.KEY));
    Assert.assertTrue(childMessage.hasContext(MultiAContext.KEY));
    Assert.assertTrue(childMessage.hasMergeableContext(AContext.KEY));
    Assert.assertTrue(childMessage.hasContext(BContext.KEY));

    multiAContext = childMessage.getContext(MultiAContext.KEY);
    Assert.assertEquals(3, multiAContext.getAContexts()
        .size());
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a1));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a2));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a3));

    AContext a4 = new AContext("4");
    childMessage.addContext(a4);
    multiAContext = childMessage.getContext(MultiAContext.KEY);
    Assert.assertTrue(childMessage.hasContext(BContext.KEY));
    Assert.assertEquals(4, multiAContext.getAContexts()
        .size());
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a1));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a2));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a3));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a4));

    multiAContext = message.getContext(MultiAContext.KEY);
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a1));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a2));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a3));
    Assert.assertFalse(multiAContext.getAContexts()
        .contains(a4));

  }
}
