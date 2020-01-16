package com.amadeus.middleware.odyssey.reactive.messaging.core;

public interface Metadata {

  /**
   * Concrete Metadata should return a key unique to its implementation. This key will be used to lookup instance
   * from the Message.
   * 
   * @return
   */
  String getMetadataKey();

  /**
   * Indicates whether this Metadata should propagate into child Messages.
   * 
   * @return true is it should, else otherwise.
   */
  boolean isMetadataPropagable();

  /**
   * Concrete Metadata should return a key unique that is shared by mergeable instances.
   * 
   * @return
   */
  String getMetadataMergeKey();

  /**
   * Merge this instance and the given Metadata into a new single instance. The instances should be of the same
   * dynamic type.
   * 
   * @param metadata
   * @return
   */
  Metadata metadataMerge(Metadata... metadata);
}
