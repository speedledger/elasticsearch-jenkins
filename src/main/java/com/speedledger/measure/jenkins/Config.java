package com.speedledger.measure.jenkins;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Elasticsearch configuration.
 * Should contain information needed to establish connection to Elasticsearch and where to index data.
 */
public class Config {
    // HTTP URL to Elasticsearch
    private String url;
    private String indexName;
    private String typeName;

    public Config(String url, String indexName, String typeName) {
        this.url = url;
        this.indexName = indexName;
        this.typeName = typeName;
    }

    public String getUrl() {
        return url;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean nonEmptyValues() {
        return url != null && !url.isEmpty() &&
                indexName != null && !indexName.isEmpty() &&
                typeName != null && !typeName.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Config that = (Config) o;

        if (indexName != null ? !indexName.equals(that.indexName) : that.indexName != null) return false;
        if (typeName != null ? !typeName.equals(that.typeName) : that.typeName != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (indexName != null ? indexName.hashCode() : 0);
        result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Config{" +
                "url='" + url + '\'' +
                ", indexName='" + indexName + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    /**
     * Converter for {@link Config}.
     */
    public static class ConverterImpl implements Converter {
        public void marshal(Object source, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
            if (source instanceof Config) {
                Config config = (Config) source;
                hierarchicalStreamWriter.addAttribute("url", config.getUrl());
                hierarchicalStreamWriter.addAttribute("indexName", config.getIndexName());
                hierarchicalStreamWriter.addAttribute("typeName", config.getTypeName());
            }
        }

        public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
            final String url = hierarchicalStreamReader.getAttribute("url");
            final String indexName = hierarchicalStreamReader.getAttribute("indexName");
            final String typeName = hierarchicalStreamReader.getAttribute("typeName");
            return new Config(url, indexName, typeName);
        }

        public boolean canConvert(Class type) {
            return Config.class == type;
        }
    }
}
