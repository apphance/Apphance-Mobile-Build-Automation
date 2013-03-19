package com.apphance.ameba.ios

import java.util.regex.Pattern

/**
 * Keeps IOS-specific configuration for the project.
 */
class IOSProjectConfiguration {

    String sdk
    String simulatorSDK
    String mainTarget
    String mainConfiguration
    File distributionDirectory
    def distributionDirectories = [:]
    File xCodeProjectDirectory
    def xCodeProjectDirectories = [:]
    List<String> targets = []
    List<String> configurations = []
    List<String> allTargets = []
    List<String> allConfigurations = []
    List<String> allIphoneSDKs = []
    List<String> allIphoneSimulatorSDKs = []
    List<String> families = []
    List<String> schemes = []
    File plistFile
    List<String> excludedBuilds = []

    boolean isBuildExcluded(String id) {
        boolean excluded = false
        excludedBuilds.each {
            Pattern p = Pattern.compile(it)
            if (p.matcher(id).matches()) {
                excluded = true
            }
        }
        excluded
    }

    Collection<Expando> getAllBuildableVariants() {
        Collection<Expando> result = []
        targets.each { t ->
            configurations.each { c ->
                def id = "$t-$c".toString()
                if (!isBuildExcluded(id)) {
                    result << new Expando(target: t, configuration: c, id: id, noSpaceId: id.replaceAll(' ', '_'))
                }
            }
        }
        result
    }

    def String getVariant(String target, String configuration) {
        "${target}-${configuration}"
    }

    def xCodeBuildExecutionPath(String target, String configuration) {
        (xCodeProjectDirectory == null || xCodeProjectDirectory == '') ?
            ['xcodebuild'] :
            ['xcodebuild', '-project', xCodeProjectDirectories[getVariant(target, configuration)]]
    }

    List<String> getXCodeBuildExecutionPath() {
        (xCodeProjectDirectory == null || xCodeProjectDirectory == '') ?
            ['xcodebuild'] :
            ['xcodebuild', '-project', xCodeProjectDirectory]
    }

    @Override
    public String toString() {
        this.properties
    }
}