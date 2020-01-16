package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMetadata;

public class MessageImplTest {

  private static class AContext implements Metadata {
    public static String KEY = "A";

    private String payload;

    public AContext(String payload) {
      this.payload = payload;
    }

    @Override
    public String getMetadataKey() {
      return KEY;
    }

    @Override
    public boolean isMetadataPropagable() {
      return true;
    }

    @Override
    public String getMetadataMergeKey() {
      return KEY;
    }

    @Override
    public Metadata metadataMerge(Metadata... metadata) {
      MultiAContext multiAContext = new MultiAContext(this);
      multiAContext.metadataMerge(metadata);
      return multiAContext;
    }

    public String getPayload() {
      return payload;
    }
  }

  private static class MultiAContext implements MutableMetadata {
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
    public String getMetadataKey() {
      return KEY;
    }

    @Override
    public boolean isMetadataPropagable() {
      return true;
    }

    @Override
    public String getMetadataMergeKey() {
      return AContext.KEY;
    }

    public List<AContext> getAContexts() {
      return new ArrayList<>(aContexts);
    }

    public void add(AContext aContext) {
      aContexts.add(aContext);
    }

    @Override
    public Metadata metadataMerge(Metadata... metadata) {
      for (Metadata mc : metadata) {
        if (mc instanceof AContext) {
          add((AContext) mc);
        } else {
          aContexts.addAll(((MultiAContext) mc).getAContexts());
        }
      }
      return this;
    }

    @Override
    public MutableMetadata createChild() {
      return new MultiAContext(this);
    }
  }

  private static class BContext implements Metadata {
    public static String KEY = "B";

    @Override
    public String getMetadataKey() {
      return KEY;
    }

    @Override
    public boolean isMetadataPropagable() {
      return true;
    }

    @Override
    public String getMetadataMergeKey() {
      return KEY;
    }

    @Override
    public Metadata metadataMerge(Metadata... metadata) {
      throw new RuntimeException("not implemented");
    }
  }

  @Test
  public void mergeContext() {
    AContext a1 = new AContext("1");
    Message<String> message = Message.<String> builder()
        .addMetadata(a1)
        .addMetadata(new BContext())
        .build();

    Assert.assertTrue(message.hasMetadata(AContext.KEY));
    Assert.assertFalse(message.hasMetadata(MultiAContext.KEY));
    Assert.assertTrue(message.hasMergeableMetadata(AContext.KEY));
    Assert.assertTrue(message.hasMetadata(BContext.KEY));

    AContext a2 = new AContext("2");
    message.addMetadata(a2);
    Assert.assertFalse(message.hasMetadata(AContext.KEY));
    Assert.assertTrue(message.hasMetadata(MultiAContext.KEY));
    Assert.assertTrue(message.hasMergeableMetadata(AContext.KEY));
    Assert.assertTrue(message.hasMetadata(BContext.KEY));

    MultiAContext multiAContext = message.getMetadata(MultiAContext.KEY);
    Assert.assertEquals(2, multiAContext.getAContexts()
        .size());
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a1));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a2));

    AContext a3 = new AContext("3");
    multiAContext = new MultiAContext(a3);
    message.addMetadata(multiAContext);
    Assert.assertFalse(message.hasMetadata(AContext.KEY));
    Assert.assertTrue(message.hasMetadata(MultiAContext.KEY));
    Assert.assertTrue(message.hasMergeableMetadata(AContext.KEY));
    Assert.assertTrue(message.hasMetadata(BContext.KEY));

    multiAContext = message.getMetadata(MultiAContext.KEY);
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
    Assert.assertFalse(childMessage.hasMetadata(AContext.KEY));
    Assert.assertTrue(childMessage.hasMetadata(MultiAContext.KEY));
    Assert.assertTrue(childMessage.hasMergeableMetadata(AContext.KEY));
    Assert.assertTrue(childMessage.hasMetadata(BContext.KEY));

    multiAContext = childMessage.getMetadata(MultiAContext.KEY);
    Assert.assertEquals(3, multiAContext.getAContexts()
        .size());
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a1));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a2));
    Assert.assertTrue(multiAContext.getAContexts()
        .contains(a3));

    AContext a4 = new AContext("4");
    childMessage.addMetadata(a4);
    multiAContext = childMessage.getMetadata(MultiAContext.KEY);
    Assert.assertTrue(childMessage.hasMetadata(BContext.KEY));
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

    multiAContext = message.getMetadata(MultiAContext.KEY);
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
