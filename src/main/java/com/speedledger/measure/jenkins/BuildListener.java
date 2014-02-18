package com.speedledger.measure.jenkins;

import com.google.common.collect.Sets;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Plugin;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.core.Index;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Send Jenkins build information to Elasticsearch.
 */
@Extension
public class BuildListener extends RunListener<Run> {
    private static final Logger LOG = Logger.getLogger(BuildListener.class.getName());

    private final JestClient jestClient;
    private Config config;

    public BuildListener() {
        LOG.info("Initializing");

        final JestClientFactory factory = new JestClientFactory();
        ClientConfig clientConfig = new ClientConfig.Builder(new ArrayList<String>()).multiThreaded(true).build();
        factory.setClientConfig(clientConfig);
        jestClient = factory.getObject();
    }

    public synchronized void loadConfig() {
        final Plugin plugin = Jenkins.getInstance().getPlugin("elasticsearch-jenkins");
        if (plugin instanceof ElasticsearchPlugin) {
            ElasticsearchPlugin elasticsearchPlugin = (ElasticsearchPlugin) plugin;

            Config newConfig = elasticsearchPlugin.getConfig();
            if (newConfig == null && config != null) {
                config = newConfig;
                LOG.info("Clearing configuration");
                jestClient.setServers(new HashSet<String>());
            } else if (newConfig != null && !newConfig.equals(config)) {
                config = newConfig;
                LOG.info("Applying new configuration: " + newConfig);
                jestClient.setServers(Sets.newHashSet(config.getUrl()));
            }
        } else {
            LOG.info("Could not find correct plugin");
        }
    }

    @Override
    public void onCompleted(Run run, TaskListener listener) {
        loadConfig();

        final boolean validConfig = config != null && config.nonEmptyValues();
        if (validConfig) {
            try {
                final Build build = createBuild(run, listener);
                final Index index = new Index.Builder(build).index(config.getIndexName()).type(config.getTypeName()).build();

                final JestResult result = jestClient.execute(index);

                if (result.isSucceeded()) {
                    LOG.fine("Sent build to Elasticsearch: " + build);
                } else {
                    LOG.warning("Failed to index build, got error message: " + result.getErrorMessage());
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error", e);
            }
        } else {
            LOG.fine("The configuration is not valid, can not index the build");
        }
    }

    private Build createBuild(Run run, TaskListener listener) {
        final Job job = run.getParent();

        EnvVars environment = null;
        try {
            environment = run.getEnvironment(listener);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Error getting environment", e);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Error getting environment", e);
        }

        final Build build = new Build();
        build.setDuration(run.getDuration());
        build.setJobName(job.getFullName());
        build.setResult(run.getResult().toString());
        build.setStartTime(run.getStartTimeInMillis());
        build.setNumber(run.getNumber());
        build.setEnvironment(environment);
        build.setSystemProperties(System.getProperties());

        return build;
    }
}