package io.github.lvyahui8.sdk.logging.factory;

import io.github.lvyahui8.sdk.logging.configuration.DefaultLogConfiguration;
import io.github.lvyahui8.sdk.logging.logger.ModuleLogger;
import io.github.lvyahui8.sdk.logging.logger.ModuleLoggerRepository;
import io.github.lvyahui8.sdk.logging.logger.impl.DefaultModuleLoggerImpl;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2020/9/20
 */
public abstract class AbstractLoggerFactory implements ModuleLoggerFactory {

    protected DefaultLogConfiguration configuration;

    public AbstractLoggerFactory(DefaultLogConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initModuleLogger(Class<? extends ModuleLogger> moduleEnumClass) {
        if (! moduleEnumClass.isEnum()) {
            throw new RuntimeException("The log module class must be an enum.");
        }
        /* 使用代理类替换代理枚举实现 */
        for (Object enumInstance : moduleEnumClass.getEnumConstants()) {
            Enum<?> loggerEnum  = (Enum<?>) enumInstance;
            ModuleLogger realModuleLogger = new DefaultModuleLoggerImpl(
                    createSlf4jLogger(loggerEnum.name(), "general",configuration.getGeneralLogPattern()),
                    createSlf4jLogger(loggerEnum.name() ,"monitor",configuration.getMonitorLogPattern()),
                    configuration.getFieldSeparator());
            ModuleLoggerRepository.put(loggerEnum.name(), realModuleLogger);
        }
    }

    private Logger createSlf4jLogger(String moduleName, String logType, String logPattern) {
        if (! configuration.getFileName().contains("$module") || ! configuration.getFileName().contains("$logType")) {
            throw new RuntimeException("Illegal file name declaration :" + configuration.getFileName());
        }
        String storagePath = (configuration.getStoragePath() == null ?
                System.getProperty("user.home") : configuration.getStoragePath()) + File.separator + "logs";

        String fileName = storagePath + File.separator +
                configuration.getFileName().replaceAll("\\$module", moduleName).replaceAll("\\$logType",logType) + ".log";
        String fileNamePattern = fileName + configuration.getFileRollingPattern();

        File file = new File(fileName);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new RuntimeException("No permission to create log path!");
        }

        return createSlf4jLogger0(logPattern, moduleName + '-' + logType, LoggerFactory.getILoggerFactory(),
                fileName, fileNamePattern);
    }

    abstract org.slf4j.Logger createSlf4jLogger0(String pattern, String loggerName, ILoggerFactory loggerFactory, String fileName, String fileNamePattern);
}