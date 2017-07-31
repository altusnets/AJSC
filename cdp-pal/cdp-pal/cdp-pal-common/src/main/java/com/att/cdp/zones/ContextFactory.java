/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;

import com.att.cdp.exceptions.NoProviderFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.pal.i18n.Msg;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * The client of the rime API uses this factory object to obtain an initialized context to a specific provider instance.
 * <p>
 * The context factory dynamically locates all RIME providers and makes them available to the client. The client
 * requests a context object for a specific provider. The context object allows the client to access that provider's
 * services and to make requests of that provider. The operation of locating the provider and obtaining a context is
 * combined into one factory method {@link com.att.cdp.zones.ContextFactory#getContext(String, Properties)}. The client
 * does not interface with the provider object directly, although they can obtain access to the provider from any
 * context object that it creates.
 * </p>
 * <p>
 * The context must be located using a provider name, and an optional properties object. The name is used to locate the
 * provider implementation. Providers may be created and added to the class path. These providers publish their
 * existence using the Java {@link ServiceLoader} facility. The way this works is that provider implementations must
 * implement the {@link Provider} interface and explicitly name the class that implements that interface in a resource
 * file named <code>com.att.cdp.zones.Provider</code> located under <code>/META-INF/services</code>. The file name of
 * the resource file is the fully qualified name of the {@link Provider} interface. The file is a text file that simply
 * contains the fully qualified class name that implements the interface.
 * </p>
 * <p>
 * The {@link ServiceLoader} simply scans the class path for all implementations and returns them using an iterator. The
 * context factory loads the provider properties (a set of default and configuration properties for each provider) and
 * checks the provider name against the name requested. When a provider is found that matches the name requested, that
 * provider is used to create the configured context.
 * </p>
 * <p>
 * Providers may need information from the client in order to configure a context. The exact type and content of this
 * information may vary from one provider to another. Additionally, some providers may be able to initialize themselves,
 * maybe only partially, from default values. Therefore, the framework allows for default properties (stored in the
 * <code>Provider.properties</code> resource file) to be merged with optional properties that the client may supply, and
 * also merge with the system properties. By merging all of these properties together, it provides the ability for
 * command-line overrides of configuration settings.
 * </p>
 * <p>
 * The merged properties are then used to configure the specific context. Once configured, each context maintains a
 * reference to the merged properties object that was used to configure them. This allows these properties to be queried
 * after instantiation.
 * </p>
 * <p>
 * An example of client code that requests a specific provider and initializes a context is shown below. By convention,
 * the properties required by a specific provider are defined as public constants in that class or interface.
 * </p>
 * 
 * <pre>
 * Properties properties = new Properties();
 * properties.setProperty(Context.PROPERTY_IDENTITY_URL, identityUrl);
 * properties.setProperty(Context.PROPERTY_COMPUTE_URL, computeUrl);
 * properties.setProperty(Context.PROPERTY_IMAGE_URL, imageUrl);
 * properties.setProperty(Context.PROPERTY_VOLUME_URL, volumeUrl);
 * properties.setProperty(Context.PROPERTY_TENANT_NAME, tenant);
 * 
 * context = ContextFactory.getContext(&quot;OpenStackProvider&quot;, properties);
 * </pre>
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */
public final class ContextFactory {

    /**
     * The service loader is used to load all implementations of the <code>Provider</code> interface dynamically. This
     * service loader searches the class path lazily for any implementations of this class. Each provider must contain a
     * resource file named <code>META-INF/services/com.att.cdp.zones.spi.Provider</code> that contains the fully
     * qualified name of the implementation class. Each provider implementation must define this resource, and multiple
     * implementations may be placed on the class path.
     */
    private static ServiceLoader<Provider> loader = ServiceLoader.load(Provider.class);

    /**
     * The logger used to log events
     */
    private static Logger logger = ConfigurationFactory.getConfiguration().getServerLogger();

    /**
     * Define the URL to reach the compute service on the provider.
     */
    public static final String PROPERTY_COMPUTE_URL = "provider.compute.url";

    /**
     * Define the URL to reach the identity service. Typically, the identity service is capable of locating all of the
     * other services, usually through a mechanism such as a "service catalog".
     */
    public static final String PROPERTY_IDENTITY_URL = "provider.url";

    /**
     * Define the URL to reach the image service on the provider.
     */
    public static final String PROPERTY_IMAGE_URL = "provider.image.url";

    /**
     * Define the URL to reach the network service on the provider.
     */
    public static final String PROPERTY_NETWORK_URL = "provider.network.url";

    /**
     * Define the URL to reach the persistent object service on the provider.
     */
    public static final String PROPERTY_OBJECT_URL = "provider.object.url";

    /**
     * Any provider that implements a proxy/agent connection can use this property to supply the location of the agent
     * for that provider. This is used to tell the proxy how to locate and connect to the agent.
     */
    public static final String PROPERTY_AGENT_URL = "provider.agent.url";

    /**
     * Define the password for authentication of the user
     */
    public static final String PROPERTY_PASSWORD = "provider.password";

    /**
     * This may be required by some providers in order to define the version of the provider that will be used. In some
     * cases, the provider may be able to determine this automatically. When this property is included, it will override
     * any automatic detection that the provider may perform and the indicated version will be used. The value for this
     * property is provider-specific. If the value is not recognized, the action is also provider-specific. Some
     * providers may be able to go ahead and use automatic detection, whereas others may fail the connection request.
     */
    public static final String PROPERTY_PROVIDER_VERSION = "provider.version";

    /**
     * Define an optional region for the provider. Not all providers will use this, but if one does, this is the
     * property that will provide that information.
     */
    public static final String PROPERTY_REGION = "provider.region";

    /**
     * Define the tenant name to connect to the provider
     */
    public static final String PROPERTY_TENANT = "provider.tenant";

    /**
     * Define the user to authenticate to the provider
     */
    public static final String PROPERTY_USERID = "provider.user";

    /**
     * Define the URL to reach the volume service on the provider.
     */
    public static final String PROPERTY_VOLUME_URL = "provider.volume.url";

    /**
     * The URL to reach the orchestration service on the provider, if installed.
     */
    public static final String PROPERTY_STACK_URL = "provider.stack.url";

    /**
     * The web URL resource path to locate the SAML STS (Secure Token Service) on the identity service URL. If SAML is
     * being used, then this resource path is appended to the identity service URL to access the secure token service
     * (if SAML tokens are being used).
     */
    public static final String PROPERTY_SAML_STS_RESOURCE = "provider.saml.sts.resource";

    /**
     * The host name of a http proxy to be used, or empty/null if no proxy (direct connection)
     */
    public static final String PROPERTY_PROXY_HOST = "http.proxyHost";

    /**
     * The port number of the http proxy if defined. If http.proxyHost is not defined, this property is ignored.
     */
    public static final String PROPERTY_PROXY_PORT = "http.proxyPort";

    /**
     * The list of trusted host names. This is a comma-separated list of host names or ip addresses. Wild card
     * characters "*" and "+" are allowed to match any number and any single character, respectively. A single asterisk
     * (*) will match all hosts. If a certificate is presented by any system that matches any entry in the trusted hosts
     * list, then it is to be accepted regardless of the certificate (self-signed, expired, invalid CN, etc). If the
     * host is not in the trusted hosts list, then the certificate must be valid. It may be self signed if the property
     * {@link #PROPERTY_ALLOW_SELF_SIGNED_CERT} is set to true.
     */
    public static final String PROPERTY_TRUSTED_HOSTS = "provider.trusted.hosts";

    /**
     * This property allows a provider to accept valid certificates that specify a CN (Common Name) that is different
     * from the host name that provided the certificate. This is the "old" way that certificates were used, and many
     * hosts are still managed that way (i.e., a certificate is purchased and installed on every server in a cluster).
     * The current SSL/TLS specifications require that every server has its own certificate and that the CN in the
     * certificate MATCH the host name that provided the certificate. While this is more secure, it requires a separate
     * certificate on each server, and each certificate to be created only for that server. This is more costly,
     * requires more administration, and still not universally done.
     */
    public static final String PROPERTY_RELAX_CERT_HOSTNAME_CHECK = "provider.relax.hostname.check";

    /**
     * This property, if defined as <code>true</code>, instructs the provider to accept self-signed certificates for
     * authentication if they are provided. If set to false or not provided (the default is false), then self-signed
     * certificates are not accepted. It can also be set to a comma-separated list of issuer CN's (common names), or
     * regular expression patterns that can match issuer CN's, that specify exact self-signed certificates to be
     * accepted. Any self-signed certificate that has an issuer CN that matches any of these entries is accepted. The
     * value true is equivalent to a regular expression pattern of "*".
     */
    public static final String PROPERTY_ALLOW_SELF_SIGNED_CERT = "provider.allow.self.signed.cert";

    /**
     * The path and name of the trust store to be used. If not specified, the trust store used is the default trust
     * store provided with the JVM, located at ${java.home}/lib/security. This property, if specified, is either an
     * absolute or relative path, and file name, of the trust store to be used.
     */
    public static final String PROPERTY_TRUST_STORE = "provider.trust.store";

    /**
     * The pass phrase needed to load and open the trust store. If not specified, then no pass phrase is used. If the
     * trust store is protected by a pass phrase, the attempt to load and use it will fail. If no trust store is
     * specified, then the default trust store and pass phrase are used. This property may be used to override the pass
     * phrase for the default trust store if desired (the ${link {@link #PROPERTY_TRUST_STORE} is NOT specified, but
     * this property is).
     */
    public static final String PROPERTY_TRUST_STORE_PASSPHRASE = "provider.trust.store.passphrase";

    /**
     * A list of comma-separated Common Names (CN) of certificate subjects that are implicitly trusted.
     */
    public static final String PROPERTY_TRUST_SUBJECTS = "provider.trust.subject";

    /**
     * If defined, supplies a comma-separated list of CN names, or regular expressions that can be used to match a CN
     * name, of the subject of a certificate. If any match the certificate being processed by the TrustManager, then the
     * certificate and the entire chain are formatted and dumped to the log in a format that allows them to be added to
     * the trust store if desired.
     */
    public static final String PROPERTY_TRUST_DUMP_CERTS = "provider.trust.dump.cert";

    /**
     * Disable proxy for the provider
     */
    public static final String PROPERTY_DISABLE_PROXY = "provider.disableHttpProxy";

    /**
     * This is an optional property that can be used to force the client connector to a specific class, rather than rely
     * on the service loader mechanism to locate and load the class.
     */
    public static final String PROPERTY_CLIENT_CONNECTOR_CLASS = "provider.client.connector.class";

    /**
     * The maximum number of times that the provider will retry to connect. If not specified, the default is 1.
     */
    public static final String PROPERTY_RETRY_LIMIT = "provider.retry.limit";

    /**
     * The amount of time, in seconds, that a provider will wait between retries. If not specified, it defaults to 0 (no
     * wait).
     */
    public static final String PROPERTY_RETRY_DELAY = "provider.retry.delay";

    /**
     * Define the version to reach the compute service on the provider.
     */
    public static final String PROPERTY_COMPUTE_VERSION = "provider.compute.version";

    /**
     * Define the version to reach the identity service. Typically, the identity service is capable of locating all of
     * the other services, usually through a mechanism such as a "service catalog".
     */
    public static final String PROPERTY_IDENTITY_VERSION = "provider.identity.version";

    /**
     * Define the version to reach the image service on the provider.
     */
    public static final String PROPERTY_IMAGE_VERSION = "provider.image.version";

    /**
     * Define the version to reach the network service on the provider.
     */
    public static final String PROPERTY_NETWORK_VERSION = "provider.network.version";

    /**
     * Define the version to reach the persistent object service on the provider.
     */
    public static final String PROPERTY_OBJECT_VERSION = "provider.object.version";

    /**
     * Define the version to reach the volume service on the provider.
     */
    public static final String PROPERTY_VOLUME_VERSION = "provider.volume.version";

    /**
     * The version to reach the orchestration service on the provider, if installed.
     */
    public static final String PROPERTY_STACK_VERSION = "provider.stack.version";

    /**
     * This property, if defined as <code>true</code> indicates that the provider should use any internal or protected
     * access mechanisms to connect to the provider, if supported by the provider. If the provider has no such concept
     * then the property is ignored. If the property is not defined or defaulted, the provider will use only public,
     * generally available access mechanisms. If not specified, the property defaults to false.
     */
    public static final String PROPERTY_USE_INTERNAL_CONNECTION = "provider.use.internal.connection";

    /**
     * This method is a special case that allows the caller to instantiate the provider directly by supplying the class
     * of the provider to be used. This can allow a caller that knows the implementation class, and which has that class
     * available to it on the class path, direct instantiation and avoiding the use of the service loader mechanism.
     * 
     * @param providerClass
     *            The class of the provider to be used
     * @param properties
     *            The configuration properties to establish a context
     * @return The context, or null
     * @throws ZoneException
     *             If the provider cannot be used for some reason.
     */
    public static Context getContext(Class<? extends Provider> providerClass, Properties properties)
        throws ZoneException {
        if (providerClass == null) {
            throw new NoProviderFoundException("No provider class was supplied, provider "
                + "context cannot be instantiated");
        }

        try {
            Provider provider = (Provider) providerClass.newInstance();
            Context ctx = provider.openContext(properties);
            logger.info(EELFResourceManager.format(Msg.ZONE_CONTEXT_CREATED, provider.getName()));
            return ctx;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new NoProviderFoundException(String.format("Provider class %s cannot be instantiated",
                providerClass.getSimpleName()), e);
        }
    }

    /**
     * Attempt to locate the service provider bootstrap class. When we locate the provider bootstrap, ask it to return a
     * new context object for the caller.
     * 
     * @param providerName
     *            The name of the provider that we are interested in using
     * @param properties
     *            An optional <code>Properties</code> object that can be used to configure the context, if needed. It
     *            can be null if no configuration is needed. The use of a configuration object is specific to the
     *            provider, some providers may require it while others may not.
     * @return The context object that allows access to that provider
     * @throws NoProviderFoundException
     *             If the provider cannot be loaded
     */
    public static Context getContext(String providerName, Properties properties) throws NoProviderFoundException {

        Iterator<Provider> providers = loader.iterator();
        String msg = String.format("Attempting to locate provider named [%s]", providerName);
        logger.debug(msg);
        try {
            while (providers.hasNext()) {
                Provider provider = providers.next();
                logger.debug(String.format("Examining provider [%s]", provider.getName()));
                if (provider.getName().equals(providerName)) {
                    Context ctx = provider.openContext(properties);
                    logger.info(EELFResourceManager.format(Msg.ZONE_CONTEXT_CREATED, providerName));
                    return ctx;
                }
            }
        } catch (ServiceConfigurationError e) {
            msg = EELFResourceManager.format(Msg.ZONE_PROVIDER_NOT_FOUND, providerName);
            throw new NoProviderFoundException(msg, e);
        }

        msg = EELFResourceManager.format(Msg.ZONE_PROVIDER_NOT_FOUND, providerName);
        logger.error(msg);
        throw new NoProviderFoundException(msg);
    }

    /**
     * Lists all the Providers
     * 
     * @return Provider Types
     * @throws NoProviderFoundException
     *             If the provider cannot be loaded
     */
    public static List<String> getProviders() throws NoProviderFoundException {

        Iterator<Provider> providers = loader.iterator();
        if (!providers.hasNext()) {
            String msg = EELFResourceManager.format(Msg.ZONE_PROVIDER_NOT_FOUND);
            logger.error(msg);
            throw new NoProviderFoundException(msg);
        }
        logger.debug("Attempting to locate All providers");
        List<String> providerTypes = new ArrayList<>();
        while (providers.hasNext()) {
            Provider provider = providers.next();
            providerTypes.add(provider.getName());
        }

        return providerTypes;
    }

    /**
     * This private constructor will prevent anyone from instantiating the context factory. This class is intended to
     * provide static access to the method to obtain the context.
     */
    private ContextFactory() {

    }
}
