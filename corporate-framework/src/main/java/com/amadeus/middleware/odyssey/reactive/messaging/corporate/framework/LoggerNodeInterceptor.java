package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.After;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Before;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeInterceptor;

public class LoggerNodeInterceptor implements NodeInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(LoggerNodeInterceptor.class);

  private String nodeName;

  @Inject
  private Message<?> message;

  @Override
  public void initialize(String nodeName) {
    this.nodeName = nodeName;
  }

  @Before
  public void before() {
    logger.info("before: {} message={}", nodeName, message);
  }

  @After
  public void after(Message<?> message) {
    logger.info("after: {}", nodeName);
  }
}
