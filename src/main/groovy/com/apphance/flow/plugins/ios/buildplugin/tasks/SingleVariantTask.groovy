package com.apphance.flow.plugins.ios.buildplugin.tasks

import com.apphance.flow.configuration.ios.variants.AbstractIOSVariant
import com.apphance.flow.plugins.ios.buildplugin.IOSSingleVariantBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

import static com.apphance.flow.plugins.FlowTasksGroups.FLOW_BUILD

class SingleVariantTask extends DefaultTask {

    String group = FLOW_BUILD
    String description = 'Builds single variant for iOS.'

    @Inject IOSSingleVariantBuilder builder

    AbstractIOSVariant variant

    @TaskAction
    void buildSingleVariant() {
        if (variant != null)
            builder.buildVariant(variant)
        else
            logger.lifecycle('Variant builder not executed - null variant passed')
    }
}
