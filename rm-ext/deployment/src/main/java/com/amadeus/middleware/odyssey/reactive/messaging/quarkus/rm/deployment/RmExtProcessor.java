package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class RmExtProcessor {

    private static final String FEATURE = "rm-ext";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

}
