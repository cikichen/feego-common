package io.github.lvyahui8.core.logging;

import org.slf4j.Logger;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2020/9/18
 */
public interface EnumModuleLogger extends ModuleLogger{
    @Override
    default Logger getInnerLogger() {
        if (!(this instanceof Enum<?>)) {
            throw new UnsupportedOperationException("This interface must be implemented by Enum<?>.");
        }
        Enum<?> em = (Enum<?>) this;
        return ModuleLoggerRepository.getModuleLogger(em.name());
    }
}
