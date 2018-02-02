package org.vaadin.grails.data.util.converter

import com.vaadin.v7.data.util.converter.Converter
import org.vaadin.grails.util.GrailsUtils

/**
 * A factory for automatically conversions.
 *
 * @see {@link com.vaadin.v7.server.VaadinSession#setConverterFactory(com.vaadin.v7.data.util.converter.ConverterFactory)}
 * @author Stephan Grundner
 * @since 2.0
 */
class GrailsAwareConverterFactory extends com.vaadin.v7.data.util.converter.DefaultConverterFactory {

    @Override
    protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        if (presentationType == String && modelType == Date) {
            return new StringToDateConverter()
        }

//        @since 2.2
        if (presentationType == String && GrailsUtils.isDomainClass(modelType)) {
            return new StringToDomainObjectConverter(modelType)
        }
        return super.findConverter(presentationType, modelType)
    }
}
