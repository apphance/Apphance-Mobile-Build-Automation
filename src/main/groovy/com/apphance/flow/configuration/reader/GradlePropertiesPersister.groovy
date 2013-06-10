package com.apphance.flow.configuration.reader

import com.apphance.flow.configuration.AbstractConfiguration
import com.apphance.flow.configuration.properties.AbstractProperty
import com.apphance.flow.util.SortedProperties
import com.google.common.io.Files
import javax.inject.Inject
import org.gradle.api.Project

import java.text.SimpleDateFormat

import static com.google.common.io.Files.newWriter
import static java.io.File.separator
import static java.nio.charset.StandardCharsets.UTF_8
import static org.slf4j.LoggerFactory.getLogger

@com.google.inject.Singleton
class GradlePropertiesPersister implements PropertyPersister {

    def log = getLogger(this.class)

    private Properties props

    private File propertyFile

    public static final String FLOW_PROP_FILENAME = 'flow.properties'

    @Inject
    GradlePropertiesPersister(Project project) {
        init(project)
    }

    @Override
    void init(Project project) {
        props = new SortedProperties()
        propertyFile = new File("${project.rootDir.absolutePath}${separator}$FLOW_PROP_FILENAME")
        if (propertyFile.exists()) {
            log.info("File ${propertyFile.absolutePath} exist. Reading configuration")
            props.load(Files.newReader(propertyFile, UTF_8))
            log.info("Configuration: $props")
        } else {
            log.info("No property file. Expected: ${propertyFile.absolutePath}\nEmpty initial configuration")
        }
    }

    @Override
    def get(String key) {
        return props.getProperty(key)
    }

    @Override
    def save(Collection<AbstractConfiguration> configurations) {
        if (propertyFile.exists()) {
            def backupFile = propertyFile.absolutePath + timeStamp
            log.info("Making backup of old configuration: $backupFile")
            Files.copy(propertyFile, new File(backupFile))
            propertyFile.delete()
        }
        propertyFile.createNewFile()
        BufferedWriter writer = newWriter(propertyFile, UTF_8)

        writer.append("""|###########################################################
                         |# Generated by Apphance Flow system by running
                         |#    gradle prepareSetup
                         |# You can modify the file manually.
                         |# Or you can re-run the prepareSetup command
                         |# for guided re-configuration
                         |###########################################################
                         |""".stripMargin())

        configurations.each { AbstractConfiguration conf ->
            saveConf(conf, writer)
        }

        writer.flush()
        writer.close()
        log.info("New configuration written successfully")
    }

    void saveConf(AbstractConfiguration conf, Writer writer) {
        writer.append("""\n# ${conf.configurationName}
                             |# ${'=' * conf.configurationName.length()}
                             |""".stripMargin())

        writer.write("${conf.enabledPropKey}=${conf.enabled}\n".toString())

        if (conf.enabled) {
            conf.flowProperties().each { AbstractProperty prop ->
                writer.write("${prop.name}=${prop.persistentForm()}\n".toString())
            }

            conf.subConfigurations.each { saveConf(it, writer) }
        }
    }

    static String getTimeStamp() {
        new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
    }
}
