package com.apphance.ameba.plugins.ios.buildplugin.tasks

import com.apphance.ameba.configuration.ios.variants.AbstractIOSVariant
import com.apphance.ameba.configuration.ios.variants.IOSVariantsConfiguration
import com.apphance.ameba.plugins.ios.parsers.MobileProvisionParser
import com.apphance.ameba.util.Preconditions
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

import static com.apphance.ameba.plugins.AmebaCommonBuildTaskGroups.AMEBA_BUILD

@Mixin(Preconditions)
class CopyMobileProvisionTask extends DefaultTask {

    static final NAME = 'copyMobileProvision'
    String description = 'Copies mobile provision file to the user library'
    String group = AMEBA_BUILD

    @Inject
    IOSVariantsConfiguration variantsConf
    @Inject
    MobileProvisionParser mpParser

    @TaskAction
    void copyMobileProvision() {
        def userHome = System.getProperty('user.home')
        def mobileProvisionDir = "$userHome/Library/MobileDevice/Provisioning Profiles/"
        new File(mobileProvisionDir).mkdirs()

        variantsConf.variants.each { v ->
            def mobileprovision = v.mobileprovision.value
            validateBundleId(v, mobileprovision)
            ant.copy(file: mobileprovision.absolutePath, todir: mobileProvisionDir, overwrite: true, failonerror: true, verbose: true)
        }
    }

    private void validateBundleId(AbstractIOSVariant v, File mobileprovision) {
        validate(v.effectiveBundleId == mpParser.bundleId(mobileprovision), {
            throw new GradleException("""|Bundle Id from variant: ${v.name} (${v.effectiveBundleId})
                                         |and from mobile provision file: ${mobileprovision.absolutePath}
                                         |(${mpParser.bundleId(mobileprovision)}) do not match!""".stripMargin())
        })
    }
}
