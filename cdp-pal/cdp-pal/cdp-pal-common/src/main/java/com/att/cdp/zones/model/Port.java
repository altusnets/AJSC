/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * The Port object represents a virtual NIC (VNIC) or Network Interface Card. This is the virtual analog of the physical
 * adapter card that was added to a physical machine to provide access to network media, such as 10-Base100,
 * 10-Base1000, or fiber optic network media.
 * <p>
 * Each port (nic) is assigned a MAC (Media Access Control) address that represents that NIC uniquely on the media.
 * Media in this case is the virtual cable or fiber that would have been connected to the physical server.
 * </p>
 * <p>
 * Each port can also be assigned one or more IP addresses. This allows a server to be addressable as different values
 * on different subnets.
 * </p>
 * 
 * @since Apr 28, 2015
 * @version $Id$
 */
public class Port extends ModelObject {

    /**
     * Enumeration to document to defined states of a port.
     */
    public enum Status {
        /**
         * The port is connected, configured, and usable for communication
         */
        ONLINE,

        /**
         * The port is disconnected or powered-off and cannot be used for communication
         */
        OFFLINE,

        /**
         * The port's status is changing because of some event or operation. The final state is yet to be determined.
         */
        PENDING,

        /**
         * The port is in an unknown state and cannot be used.
         */
        UNKNOWN;
    }

    /**
     * The serial version id of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * The IP addresses associated with this port. These are NOT floating IP addresses. A floating IP address is
     * associated with the server by the provider if used. If a subnet uses DHCP, the DHCP server assigns the IP address
     * to the interface between the NIC and the subnet.
     */
    private List<String> addresses;

    /**
     * The Media Access Control (MAC) address of this NIC. MAC address is a unique address assigned to the NIC when the
     * NIC is built (hardware). Many latest generation hardware NICs also allow the user to change the MAC address. In
     * the case of SDN (virtualized), the MAC address can be changed or set.
     */
    private String macAddr;

    /**
     * The ID of the network this port is part of
     */
    private String network;

    /**
     * The id of the subnet attached to this NIC
     */
    private String subnet;

    /**
     * The ID of this NIC. This is just an id assigned by the provider.
     */
    private String id;

    /**
     * The state of this NIC
     */
    private Status portState;

    /**
     * The ID of the server that owns this port, if known
     */
    private String server;

    /**
     * An optional name of the port (for providers that require that)
     */
    private String name;

    /**
     * Default constructor
     */
    public Port() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Port(Context context) {
        super(context);
    }

    /**
     * @return The list of IP addresses associated with this port
     */
    public List<String> getAddresses() {
        return addresses;
    }

    /**
     * @return The Media Access Control (MAC) address associated with this port
     */
    public String getMacAddr() {
        return macAddr;
    }

    /**
     * @return The subnet ID associated with this port
     */
    public String getSubnetId() {
        return subnet;
    }

    /**
     * @return The ID of the port itself
     */
    public String getId() {
        return id;
    }

    /**
     * @return The state of the port
     */
    public Status getPortState() {
        return portState;
    }

    /**
     * @param addresses
     *            The addresses to assign to this port
     */
    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    /**
     * @param address
     *            The address to be added to this port. If it already exists, it is not added again.
     */
    public void addAddress(String address) {
        if (addresses == null) {
            addresses = new ArrayList<String>();
        }
        if (!addresses.contains(address)) {
            addresses.add(address);
        }
    }

    /**
     * @param macAddr
     *            The MAC address of this port
     */
    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    /**
     * @param netId
     *            The subnet id of the subnet attached to this port
     */
    public void setSubnetId(String netId) {
        this.subnet = netId;
    }

    /**
     * @param id
     *            THe id of this port
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param portState
     *            The state of this port
     */
    public void setPortState(Status portState) {
        this.portState = portState;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (macAddr == null) {
            return super.hashCode();
        }
        return macAddr.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Port other = (Port) obj;

        if (macAddr == null && other.macAddr == null) {
            if (id.equals(other.id) && network.equals(other.network) && subnet.equals(other.subnet)) {
                return true;
            }
        } else if (macAddr != null && other.macAddr != null) {
            return macAddr.equals(other.macAddr);
        }
        return false;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Port %s, state=%s, MAC=%s, subnet=%s", id, portState.toString(), macAddr, subnet);
    }

    /**
     * @return the id of the server that owns this port
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server
     *            The id of the server that owns this port
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the id of the network this port is part of
     */
    public String getNetwork() {
        return network;
    }

    /**
     * @param network
     *            The id of the network that this port is part of.
     */
    public void setNetwork(String network) {
        this.network = network;
    }

    /**
     * This method allows the caller to delete a port by asking the port to delete itself
     * 
     * @throws ZoneException
     *             If the port is not connected
     */
    public void delete() throws ZoneException {
        notConnectedError();
    }

    /**
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(String name) {
        this.name = name;
    }
}
