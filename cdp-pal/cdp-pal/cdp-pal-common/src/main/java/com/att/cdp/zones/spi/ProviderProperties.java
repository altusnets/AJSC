/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

/**
 * <p>
 * This interface is used to define the symbolic names of all properties that can be defined in the
 * "provider.properties" configuration resource file which are understood and processed by the common framework. The
 * specific provider implementation can include additional properties that are not defined here. These properties are
 * simply placed into the context configuration object as-is, without any additional processing or operations by the
 * framework. The provider implementation can access these properties from the context configuration.
 * </p>
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */

public abstract class ProviderProperties {

    /**
     * The version of the client-side implementation (which the rime API implementation uses)
     */
    public static final String PROPERTY_PROVIDER_CLIENT_VERSION = "provider.client.version";

    /**
     * The base name of a resource bundle that contains messages to be issued by the provider using the RIME API I18N
     * support, if desired. If omitted, no bundle is loaded.
     */
    public static final String PROPERTY_PROVIDER_MESSAGES = "provider.message.resources";

    /**
     * The property key used to specify the provider name within the provider properties. This is ONLY examined in the
     * default properties used to load the provider and MUST be present. This is how the provider gets a name that can
     * be used by a client to request the provider.
     */
    public static final String PROPERTY_PROVIDER_NAME = "provider.name";

    /**
     * Indicates that the client supports the "compute" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_COMPUTE = "provider.supports.compute";

    /**
     * Indicates that the client supports the "identity" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_IDENTITY = "provider.supports.identity";

    /**
     * Indicates that the client supports the "image" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_IMAGE = "provider.supports.image";

    /**
     * Indicates that the client supports the "network" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_NETWORK = "provider.supports.network";

    /**
     * Indicates that the client supports the "object" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_OBJECT = "provider.supports.object";

    /**
     * Indicates that the provider allows a server to be resumed
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_RESUME_SERVER = "provider.supports.resume.server";

    /**
     * Indicates that the provider allows a server to be started
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_START_SERVER = "provider.supports.start.server";

    /**
     * Indicates that the provider allows a server to be stopped
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_STOP_SERVER = "provider.supports.stop.server";

    /**
     * Indicates that the provider allows a server to be suspended
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_SUSPEND_SERVER = "provider.supports.suspend.server";

    /**
     * Indicates that the client supports the "volume" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_VOLUME = "provider.supports.volume";

    /**
     * Indicates that the client supports the "snapshot" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_SNAPSHOT = "provider.supports.snapshot";

    /**
     * Indicates if the client supports the "stack" service abstraction
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_STACK = "provider.supports.stack";

    /**
     * Indicates if the provider is a bare metal implementation
     */
    public static final String PROPERTY_PROVIDER_BARE_METAL = "provider.bare.metal";
    
    /**
     * Indicates if the provider is a vmWare implementation
     */
    public static final String PROPERTY_PROVIDER_VMWARE_PROXY = "provider.vmware";

    /**
     * The provider supports load balancers
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_LB = "provider.supports.lb";

    /**
     * The provider supports firewalls
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_FW = "provider.supports.fw";

    /**
     * The provider supports the ability to move a server from one physical host to another. Move allows a instance that
     * is down (likely a failed physical host) to be moved to a new physical host.
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_MOVE_SERVER = "provider.supports.move.server";

    /**
     * The provider supports the ability to migrate a server. Migration works with a running server and honors any
     * policies or profiles that the provider may have to control the location and hosts that the server ends up on.
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_MIGRATE_SERVER = "provider.supports.migrate.server";

    /**
     * The provider supports the ability to migrate a server. Migration works with a running server and honors any
     * policies or profiles that the provider may have to control the location and hosts that the server ends up on.
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_KEYPAIR_READ = "provider.supports.keypair.read";

    /**
     * The provider supports the ability to use a configured user id for ssh
     */
    public static final String PROPERTY_PROVIDER_CONFIGURATION_KEYS = "provider.configuration.keys";

    /**
     * The provider supports the ability to use floating ip pool
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_FLOATING_IP_POOL = "provider.supports.floatingippool";

    /**
     * The provider supports the ability to to ssh
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_SSH = "provider.supports.ssh";

    /**
     * The provider supports the generated hostname
     */
    public static final String PROPERTY_PROVIDER_HOSTNAME_GENERATED = "provider.hostname.generated";

    /**
     * The provider supports the generated getMetadata
     */
    public static final String PROPERTY_PROVIDER_GETNETWORKMETADATA = "provider.supports.networkMetadata";

    /**
     * The provider supports the generated abort resize
     */
    public static final String PROPERTY_PROVIDER_ABORT_RESIZE = "provider.supports.abortresize";

    /**
     * This provider supports the ability to use Access Control Lists
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_ACL = "provider.supports.acl";

    /**
     * This provider supports the ability to use Templates
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_TEMPLATE = "provider.supports.template";

    /**
     * This provider supports the ability to rebuild server.
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_REBUILD_SERVER = "provider.supports.rebuild.server";

    /**
     * This provider supports the ability to delete networks
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_NETWORK_DELETE = "provider.supports.network.delete";

    /**
     * This provider supports the ability to create networks
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_NETWORK_CREATE = "provider.supports.network.create";

    /**
     * This provider supports the ability to delete subnets
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_SUBNET_DELETE = "provider.supports.subnet.delete";

    /**
     * This provider supports the ability to create subnets
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_SUBNET_CREATE = "provider.supports.subnet.create";

    /**
     * This provider supports the ability to assign an IP to a VM globally, or to a specific NIC
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_ASSIGN_IP = "provider.supports.assign.ip";

    /**
     * This provider supports the ability to release (remove) an IP from a VM or a specific NIC
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_RELEASE_IP = "provider.supports.release.ip";

    /**
     * Indicates that the client supports the "hypervisor" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_HYPERVISOR = "provider.supports.hypervisor";
    
    /**
     * Indicates that the client supports the "update volume" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_UPDATE_VOLUME = "provider.supports.update.volume";
    
    /**
     * Indicates that the client supports the "get volume by server" service abstraction if true
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_GET_VOLUMES_BYSERVER = "provider.supports.get.volumebyserver";

    /**
     * Indicates that the provider supports the ability to create a port with a model port object supplying the detailed
     * characteristics of the desired port, such as fixed ip addresses and/or mac address.
     */
    public static final String PROPERTY_PROVIDER_SUPPORTS_CREATE_PORT_FROM_MODEL =
        "provider.supports.create.port.from.model";

    /**
     * Private default ctor prevents instantiation
     */
    private ProviderProperties() {
        // empty
    }
}
