package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import org.junit.Assert;
import org.junit.Test;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public class AbstractMessageBuilderTest {

  @Test
  public void basic() {
    Message<String> message = new TestMessageBuilder<String>().build();
    Assert.assertNotNull(message);
    Assert.assertFalse(message.getMessageAck()
        .isDone());
  }

  @Test
  public void oneParentOneChild() {
    Message<String> parent = new TestMessageBuilder<String>().build();

    Message<String> child = new TestMessageBuilder<String>().fromParents(parent)
        .build();

    Assert.assertFalse(parent.getMessageAck()
        .isDone());
    Assert.assertFalse(parent.getStagedAck()
        .isDone());
    Assert.assertFalse(child.getMessageAck()
        .isDone());
    Assert.assertFalse(child.getStagedAck()
        .isDone());

    parent.getStagedAck()
        .complete(null);
    Assert.assertFalse(parent.getMessageAck()
        .isDone());
    Assert.assertTrue(parent.getStagedAck()
        .isDone());
    Assert.assertFalse(child.getMessageAck()
        .isDone());
    Assert.assertFalse(child.getStagedAck()
        .isDone());

    child.getStagedAck()
        .complete(null);
    Assert.assertTrue(parent.getMessageAck()
        .isDone());
    Assert.assertTrue(parent.getStagedAck()
        .isDone());
    Assert.assertTrue(child.getMessageAck()
        .isDone());
    Assert.assertTrue(child.getStagedAck()
        .isDone());
  }

  @Test
  public void twoParentOneChild() {
    Message<String> parent1 = new TestMessageBuilder<String>().build();

    Message<String> parent2 = new TestMessageBuilder<String>().build();

    Message<String> child = new TestMessageBuilder<String>().fromParents(parent1, parent2)
        .build();

    Assert.assertFalse(parent1.getMessageAck()
        .isDone());
    Assert.assertFalse(parent1.getStagedAck()
        .isDone());
    Assert.assertFalse(parent2.getMessageAck()
        .isDone());
    Assert.assertFalse(parent2.getStagedAck()
        .isDone());
    Assert.assertFalse(child.getMessageAck()
        .isDone());
    Assert.assertFalse(child.getStagedAck()
        .isDone());

    parent1.getStagedAck()
        .complete(null);
    Assert.assertFalse(parent1.getMessageAck()
        .isDone());
    Assert.assertTrue(parent1.getStagedAck()
        .isDone());
    Assert.assertFalse(parent2.getMessageAck()
        .isDone());
    Assert.assertFalse(parent2.getStagedAck()
        .isDone());
    Assert.assertFalse(child.getMessageAck()
        .isDone());
    Assert.assertFalse(child.getStagedAck()
        .isDone());

    child.getStagedAck()
        .complete(null);
    Assert.assertTrue(parent1.getMessageAck()
        .isDone());
    Assert.assertTrue(parent1.getStagedAck()
        .isDone());
    Assert.assertFalse(parent2.getMessageAck()
        .isDone());
    Assert.assertFalse(parent2.getStagedAck()
        .isDone());
    Assert.assertTrue(child.getMessageAck()
        .isDone());
    Assert.assertTrue(child.getStagedAck()
        .isDone());

    parent2.getStagedAck()
        .complete(null);
    Assert.assertTrue(parent1.getMessageAck()
        .isDone());
    Assert.assertTrue(parent1.getStagedAck()
        .isDone());
    Assert.assertTrue(parent2.getMessageAck()
        .isDone());
    Assert.assertTrue(parent2.getStagedAck()
        .isDone());
    Assert.assertTrue(child.getMessageAck()
        .isDone());
    Assert.assertTrue(child.getStagedAck()
        .isDone());
  }

  @Test
  public void oneParentTwoChildren() {
    Message<String> parent = new TestMessageBuilder<String>().build();
    Message<String> child1 = new TestMessageBuilder<String>().fromParents(parent)
        .build();
    Message<String> child2 = new TestMessageBuilder<String>().fromParents(parent)
        .build();

    Assert.assertFalse(parent.getMessageAck()
        .isDone());
    Assert.assertFalse(parent.getStagedAck()
        .isDone());
    Assert.assertFalse(child1.getMessageAck()
        .isDone());
    Assert.assertFalse(child1.getStagedAck()
        .isDone());
    Assert.assertFalse(child2.getMessageAck()
        .isDone());
    Assert.assertFalse(child2.getStagedAck()
        .isDone());

    parent.getStagedAck()
        .complete(null);
    Assert.assertFalse(parent.getMessageAck()
        .isDone());
    Assert.assertTrue(parent.getStagedAck()
        .isDone());
    Assert.assertFalse(child1.getMessageAck()
        .isDone());
    Assert.assertFalse(child1.getStagedAck()
        .isDone());
    Assert.assertFalse(child2.getMessageAck()
        .isDone());
    Assert.assertFalse(child2.getStagedAck()
        .isDone());

    child1.getStagedAck()
        .complete(null);
    Assert.assertFalse(parent.getMessageAck()
        .isDone());
    Assert.assertTrue(parent.getStagedAck()
        .isDone());
    Assert.assertTrue(child1.getMessageAck()
        .isDone());
    Assert.assertTrue(child1.getStagedAck()
        .isDone());
    Assert.assertFalse(child2.getMessageAck()
        .isDone());
    Assert.assertFalse(child2.getStagedAck()
        .isDone());

    child2.getStagedAck()
        .complete(null);
    Assert.assertTrue(parent.getMessageAck()
        .isDone());
    Assert.assertTrue(parent.getStagedAck()
        .isDone());
    Assert.assertTrue(child1.getMessageAck()
        .isDone());
    Assert.assertTrue(child1.getStagedAck()
        .isDone());
    Assert.assertTrue(child2.getMessageAck()
        .isDone());
    Assert.assertTrue(child2.getStagedAck()
        .isDone());
  }
}
