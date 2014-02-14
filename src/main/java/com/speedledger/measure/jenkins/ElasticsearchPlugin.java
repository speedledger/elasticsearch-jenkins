package com.speedledger.measure.jenkins;

import hudson.Plugin;
import hudson.model.Descriptor;
import hudson.model.Items;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Elasticsearch plugin to Jenkins.
 * Reports build information to Elasticsearch on build completion.
 *
 * This class only handles loading and saving the configuration of Elasticsearch, see {@link Config}.
 */
public class ElasticsearchPlugin extends Plugin {
    private static final Logger LOG = Logger.getLogger(ElasticsearchPlugin.class.getName());

    private Config config;

    @Override
    public void start() throws Exception {
        Items.XSTREAM.registerConverter(new Config.ConverterImpl());
        load();
        LOG.fine("Loading config: " + config);
    }

    @Override
    public void configure(StaplerRequest req, JSONObject jsonObject) throws IOException, ServletException, Descriptor.FormException {
        String url = jsonObject.getString("elasticsearchUrl");
        String indexName = jsonObject.getString("elasticsearchIndexName");
        String typeName = jsonObject.getString("elasticsearchTypeName");
        config = new Config(url, indexName, typeName);
        LOG.fine("Saving config: " + config);
        save();
    }

    public Config getConfig() {
        return config;
    }

    // Used by Jelly
    public String getUrl() {
        return config == null ? null : config.getUrl();
    }

    // Used by Jelly
    public String getIndexName() {
        return config == null ? null : config.getIndexName();
    }

    // Used by Jelly
    public String getTypeName() {
        return config == null ? null : config.getTypeName();
    }
}