package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final BedrockLibraryAccessors laccForBedrockLibraryAccessors = new BedrockLibraryAccessors(owner);
    private final Log4jLibraryAccessors laccForLog4jLibraryAccessors = new Log4jLibraryAccessors(owner);
    private final OkaeriLibraryAccessors laccForOkaeriLibraryAccessors = new OkaeriLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

        /**
         * Creates a dependency provider for gson (com.google.code.gson:gson)
     * with version '2.10.1'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGson() {
            return create("gson");
    }

        /**
         * Creates a dependency provider for network (org.cloudburstmc.netty:netty-transport-raknet)
     * with version '1.0.0.CR3-SNAPSHOT'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getNetwork() {
            return create("network");
    }

        /**
         * Creates a dependency provider for serialization (org.jetbrains.kotlinx:kotlinx-serialization-json)
     * with version '1.6.2'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSerialization() {
            return create("serialization");
    }

    /**
     * Returns the group of libraries at bedrock
     */
    public BedrockLibraryAccessors getBedrock() {
        return laccForBedrockLibraryAccessors;
    }

    /**
     * Returns the group of libraries at log4j
     */
    public Log4jLibraryAccessors getLog4j() {
        return laccForLog4jLibraryAccessors;
    }

    /**
     * Returns the group of libraries at okaeri
     */
    public OkaeriLibraryAccessors getOkaeri() {
        return laccForOkaeriLibraryAccessors;
    }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class BedrockLibraryAccessors extends SubDependencyFactory {

        public BedrockLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for codec (org.cloudburstmc.protocol:bedrock-codec)
         * with versionRef 'protocol'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCodec() {
                return create("bedrock.codec");
        }

            /**
             * Creates a dependency provider for connection (org.cloudburstmc.protocol:bedrock-connection)
         * with versionRef 'protocol'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getConnection() {
                return create("bedrock.connection");
        }

    }

    public static class Log4jLibraryAccessors extends SubDependencyFactory {
        private final Log4jSlf4j2LibraryAccessors laccForLog4jSlf4j2LibraryAccessors = new Log4jSlf4j2LibraryAccessors(owner);

        public Log4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for api (org.apache.logging.log4j:log4j-api)
         * with versionRef 'log4j'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getApi() {
                return create("log4j.api");
        }

            /**
             * Creates a dependency provider for core (org.apache.logging.log4j:log4j-core)
         * with versionRef 'log4j'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("log4j.core");
        }

        /**
         * Returns the group of libraries at log4j.slf4j2
         */
        public Log4jSlf4j2LibraryAccessors getSlf4j2() {
            return laccForLog4jSlf4j2LibraryAccessors;
        }

    }

    public static class Log4jSlf4j2LibraryAccessors extends SubDependencyFactory {

        public Log4jSlf4j2LibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for impl (org.apache.logging.log4j:log4j-slf4j2-impl)
         * with versionRef 'log4j'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getImpl() {
                return create("log4j.slf4j2.impl");
        }

    }

    public static class OkaeriLibraryAccessors extends SubDependencyFactory {

        public OkaeriLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (eu.okaeri:okaeri-configs-core)
         * with version 'okaeri'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("okaeri.core");
        }

            /**
             * Creates a dependency provider for snakeyaml (eu.okaeri:okaeri-configs-yaml-snakeyaml)
         * with versionRef 'okaeri'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getSnakeyaml() {
                return create("okaeri.snakeyaml");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: log4j (2.23.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLog4j() { return getVersion("log4j"); }

            /**
             * Returns the version associated to this alias: okaeri (5.0.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getOkaeri() { return getVersion("okaeri"); }

            /**
             * Returns the version associated to this alias: protocol (3.0.0.Beta12-SNAPSHOT)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getProtocol() { return getVersion("protocol"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

            /**
             * Creates a dependency bundle provider for log4j which is an aggregate for the following dependencies:
             * <ul>
             *    <li>org.apache.logging.log4j:log4j-api</li>
             *    <li>org.apache.logging.log4j:log4j-core</li>
             *    <li>org.apache.logging.log4j:log4j-slf4j2-impl</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getLog4j() {
                return createBundle("log4j");
            }

            /**
             * Creates a dependency bundle provider for okaeri which is an aggregate for the following dependencies:
             * <ul>
             *    <li>eu.okaeri:okaeri-configs-core</li>
             *    <li>eu.okaeri:okaeri-configs-yaml-snakeyaml</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getOkaeri() {
                return createBundle("okaeri");
            }

            /**
             * Creates a dependency bundle provider for protocol which is an aggregate for the following dependencies:
             * <ul>
             *    <li>org.cloudburstmc.protocol:bedrock-codec</li>
             *    <li>org.cloudburstmc.protocol:bedrock-connection</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getProtocol() {
                return createBundle("protocol");
            }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for git to the plugin id 'com.gorylenko.gradle-git-properties'
             * with version '2.4.2'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getGit() { return createPlugin("git"); }

            /**
             * Creates a plugin provider for shadow to the plugin id 'com.github.johnrengelman.shadow'
             * with version '8.0.0'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getShadow() { return createPlugin("shadow"); }

    }

}
