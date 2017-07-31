/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to contain algorithms for manipulating IP addresses.
 * 
 * @author <a href="mailto:dh868g@att.com?subject=com.att.cdp.util.IPHelper">Dewayne Hafenstein</a>
 * @since Nov 24, 2014
 * @version $Id$
 */

public final class IPHelper {

    private static final String HEX = "[0-9a-fA-F]";
    private static final String HEX4 = HEX + "{1,4}";
    private static final String DEC = "[0-9]";
    private static final String DEC3 = DEC + "{1,3}";
    private static final String VALIDIPV4 = "(" + DEC3 + "\\.){3}" + DEC3;
    private static final String VALIDIPV6 = "(" + HEX4 + "\\:){7}" + HEX4;

    /**
     * One byte of an IP address with the decimal value 10, used in checking IPv4 addresses one byte at a time for
     * private, non-routable subnets, such as 10.1.2.3
     */
    private static final byte IP_ADDRESS_10 = (byte) 10;

    /**
     * One byte of an IP address with the decimal value 192, used in checking IPv4 addresses one byte at a time for
     * private, non-routable subnets, such as 192.168.1.2
     */
    private static final byte IP_ADDRESS_192 = (byte) 192;

    /**
     * One byte of an IP address with the decimal value 168, used in checking IPv4 addresses one byte at a time for
     * private, non-routable subnets, such as 192.168.1.2
     */
    private static final byte IP_ADDRESS_168 = (byte) 168;

    /**
     * One byte of an IP address with the decimal value 172, used in checking IPv4 addresses one byte at a time for
     * private, non-routable subnets, such as 172.16.0.0 - 172.31.255.255
     */
    private static final byte IP_ADDRESS_172 = (byte) 172;

    /**
     * One byte of an IP address with the decimal value 16, used in checking IPv4 addresses one byte at a time for
     * private, non-routable subnets, such as 172.16.0.0 - 172.31.255.255
     */
    private static final byte IP_ADDRESS_16 = (byte) 16;

    /**
     * One byte of an IP address with the decimal value 31, used in checking IPv4 addresses one byte at a time for
     * private, non-routable subnets, such as 172.16.0.0 - 172.31.255.255
     */
    private static final byte IP_ADDRESS_31 = (byte) 31;

    /**
     * The maximum decimal value that can be assigned a single byte of an IPv4 dotted notation address.
     */
    private static final int IPV4_MAX_SEGMENT_VALUE = 255;

    /**
     * The number of bytes that comprise an IPv4 address
     */
    private static final int IPV4_BYTE_COUNT = 4;

    /**
     * The number of bytes that comprise an IPv6 address
     */
    private static final int IPV6_BYTE_COUNT = 16;

    /**
     * The number of dotted numeric "segments" that comprise an IPv6 address, each segment being 2-bytes
     */
    private static final int IPV6_SEGMENT_COUNT = 8;

    /**
     * The maximum decimal value that can be assigned a single segment of an IPv6 dotted notation address.
     */
    private static final int IPV6_MAX_SEGMENT_VALUE = 65535;

    /**
     * Number of bits per byte
     */
    private static final int BITS_PER_BYTE = 8;

    private static final int BYTE_MASK = 0xFF;

    /**
     * Private constructor prevents anyone from creating an instance of the class
     */
    private IPHelper() {

    }

    /**
     * Determines if the IP address specified is publicly routable (i.e., will be passed on from one subnet to another
     * by routers and bridges).
     * <p>
     * An IP address that specifies a private address range is not routed across a subnet boundary. These addresses are
     * valid only within the subnet that they originate in. This means that they can be reused on other subnets, and
     * therefore cannot be routed across subnet boundaries because they would be ambiguous. These addresses represent
     * private interfaces of one or more computer systems on a specific subnet, and are typically referred to as
     * "private" addresses.
     * </p>
     * 
     * @param ip
     *            The ip address (Ipv4 or Ipv6) to be tested.
     * @return True if the address is routable (public), and false if the address is either private or invalid.
     */
    public static boolean isRoutable(String ip) {
        if (isValidIpv6(ip)) {
            return true; // IPv6 is always routed
        }

        if (isValidIpv4(ip)) {
            byte[] bytes = convertIpv4Address(ip);

            // If it is 10.*.*.* it is private
            if (bytes[0] == IP_ADDRESS_10) {
                return false;
            }

            // If it is 192.168.*.* it is private
            if (bytes[0] == IP_ADDRESS_192 && bytes[1] == IP_ADDRESS_168) {
                return false;
            }

            // If it 172.16.*.* - 172.31.*.* it is private
            if ((bytes[0] == IP_ADDRESS_172) && (bytes[1] >= IP_ADDRESS_16) && (bytes[1] <= IP_ADDRESS_31)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Tests the given CIDR to see if it is a valid CIDR, syntactically speaking.
     * <p>
     * In order for a CIDR to be valid, the different parts of the CIDR must be present, separated by a forward slash
     * (/). The first part is a standard IP address portion, which supplies the bit pattern of the CIDR. The second
     * portion is the network id prefix length, in bits. The ip address must be valid, and the prefix must be valid for
     * the format of the IP, either v4 or v6. For a v4 network, the prefix must be between 1 and 31 inclusive, and for a
     * v6 network, between 1 and 63 inclusive.
     * </p>
     * 
     * @param cidr
     *            The CIDR to be validated
     * @return True if the CIDR is a valid CIDR, and false if not.
     */
    public static boolean isValidCIDR(String cidr) {
        boolean result = false;
        if (cidr == null || cidr.trim().length() == 0 || cidr.indexOf('/') == -1) {
            return result;
        }
        String[] parts = cidr.split("/");
        String ip = parts[0].trim();
        int prefix = Integer.parseInt(parts[1].trim());

        if (isValidIpv4(ip)) {
            if (prefix >= 1 && prefix <= 31) {
                result = true;
            }
        } else if (isValidIpv6(ip)) {
            if (prefix >= 1 && prefix <= 63) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Determines if the CIDR represents an IPv4 address range or not.
     * 
     * @param cidr
     *            The CIDR to be tested
     * @return True if the CIDR represents an IPv4 address range
     */
    public static boolean isIpv4CIDR(String cidr) {
        boolean result = false;
        if (cidr == null || cidr.trim().length() == 0 || cidr.indexOf('/') == -1) {
            return result;
        }
        String[] parts = cidr.split("/");
        String ip = parts[0].trim();

        if (isValidIpv4(ip)) {
            result = true;
        }

        return result;
    }

    /**
     * Determines if the CIDR represents an IPv6 address range or not.
     * 
     * @param cidr
     *            The CIDR to be tested
     * @return True if the CIDR represents an IPv6 address range
     */
    public static boolean isIpv6CIDR(String cidr) {
        boolean result = false;
        if (cidr == null || cidr.trim().length() == 0 || cidr.indexOf('/') == -1) {
            return result;
        }
        String[] parts = cidr.split("/");
        String ip = parts[0].trim();

        if (isValidIpv6(ip)) {
            result = true;
        }

        return result;
    }

    /**
     * Determines if the CIDR represents a routable range of addresses or not. If the CIDR is not valid, the return
     * value is also false.
     * 
     * @param cidr
     *            The CIDR to be tested, either V4 or V6.
     * @return True if the CIDR represents a routable range, and false if it is not routable or the CIDR is invalid.
     */
    public static boolean isCIDRRoutable(String cidr) {
        boolean result = false;
        if (isValidCIDR(cidr)) {
            String[] parts = cidr.split("/");
            String ip = parts[0].trim();
            result = isRoutable(ip);
        }

        return result;
    }

    /**
     * tests to see if the address is a valid IP address or not
     * 
     * @param ip
     *            The ip address to test
     * @return True if it is valid
     */
    public static boolean isValidIp(String ip) {
        if (ip == null || ip.trim().length() == 0) {
            return false;
        }

        return isValidIpv4(ip) || isValidIpv6(ip);
    }

    /**
     * Tests if the IPv4 encoding is valid
     * 
     * @param ip
     *            The ipV4 representation of the IP address
     * @return True if the representation is a valid encoding, false otherwise
     */
    public static boolean isValidIpv4(String ip) {
        if (ip == null || ip.trim().length() == 0) {
            return false;
        }

        Pattern pattern = Pattern.compile(VALIDIPV4);
        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches() && convertIpv4Address(ip) != null) {
            return true;
        }
        return false;
    }

    /**
     * Tests if the IPv6 encoding is valid
     * 
     * @param ip
     *            The ipV6 representation of the IP address
     * @return True if the representation is a valid encoding, false otherwise
     */
    public static boolean isValidIpv6(String ip) {
        if (ip == null || ip.trim().length() == 0) {
            return false;
        }

        Pattern pattern = Pattern.compile(VALIDIPV6);
        Matcher matcher = pattern.matcher(expandIpv6Address(ip));
        if (matcher.matches() && convertIpv6Address(ip) != null) {
            return true;
        }
        return false;
    }

    /**
     * Convert an IPv4 address string representation into a sequence of 4 bytes.
     * 
     * @param ip
     *            The IP address string to be converted
     * @return The array of 4 bytes corresponding to each encoded value, or a null array if the address is invalid in
     *         any way.
     */
    public static byte[] convertIpv4Address(String ip) {

        String[] tokens = ip.split("\\.");
        if (tokens.length != IPV4_BYTE_COUNT) {
            return null;
        }
        byte[] bytes = new byte[IPV4_BYTE_COUNT];
        for (int index = 0; index < IPV4_BYTE_COUNT; index++) {
            try {
                int value = Integer.parseInt(tokens[index]);
                if (value < 0 || value > IPV4_MAX_SEGMENT_VALUE) {
                    return null;
                }
                bytes[index] = (byte) value;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return bytes;
    }

    /**
     * Convert an IPv6 address string representation into a sequence of 16 bytes.
     * 
     * @param ip
     *            The IP address string to be converted
     * @return The array of 16 bytes corresponding to each encoded value, or a null array if the address is invalid in
     *         any way.
     */
    public static byte[] convertIpv6Address(String ip) {
        String temp = expandIpv6Address(ip);
        String[] tokens = temp.split("\\:");
        if (tokens.length != IPV6_SEGMENT_COUNT) {
            return null;
        }
        byte[] bytes = new byte[IPV6_BYTE_COUNT];
        for (int index = 0, byteIndex = 0; index < IPV6_SEGMENT_COUNT; index++, byteIndex += 2) {
            try {
                int value = Integer.parseInt(tokens[index].toLowerCase(), IPV6_BYTE_COUNT);
                if (value < 0 || value > IPV6_MAX_SEGMENT_VALUE) {
                    return null;
                }
                bytes[byteIndex] = (byte) (value >> BITS_PER_BYTE);
                bytes[byteIndex + 1] = (byte) (value & BYTE_MASK);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return bytes;
    }

    /**
     * <p>
     * Expand an IPv6 address that uses the shorthand zero-fill notation to be the full 16-byte specification. This is
     * because IPv6 addresses can be quite long and tedious to write, so the standard allows for redundant, adjacent
     * zero pairs to be eliminated and indicated by the use of "::".
     * </p>
     * 
     * @param ip
     */
    public static String expandIpv6Address(String ip) {
        if (ip == null || ip.trim().length() == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer(ip.trim());

        /*
         * Determine if there is any zero-fill (::) shorthand notation in the address. If there is, we need to expand
         * the address by insertion of zeroes in the address to replace the shorthand notation to fill it.
         */
        int zeroFill = buffer.indexOf("::");
        if (zeroFill != -1) {
            /*
             * Next, count the number of bit groups already in the address (ignore the zero fill, which will be a null
             * token)
             */
            String[] groups = ip.split("\\:");
            int count = 0;
            for (String group : groups) {
                if (group != null && group.trim().length() > 0) {
                    count++;
                }
            }

            /*
             * Compute the count of bit groups to be inserted, if any
             */
            if (count < IPV6_SEGMENT_COUNT) {
                buffer.delete(zeroFill, zeroFill + 2);
                for (; count < IPV6_SEGMENT_COUNT; count++) {
                    buffer.insert(zeroFill, "0:");
                }
                buffer.insert(zeroFill, ":");
            }
            if (buffer.charAt(buffer.length() - 1) == ':') {
                buffer.delete(buffer.length() - 1, buffer.length());
            }
            if (buffer.charAt(0) == ':') {
                buffer.delete(0, 1);
            }
        }

        return buffer.toString();
    }

    /**
     * This method normalizes an IPv6 address by removing leading zeroes from segments, and by replacing any consecutive
     * zero segments with ::.
     * 
     * @param address
     *            The IPv6 address to be normalized
     * @return The normalized IPv6 address
     */
    public static String normalizeIpv6(String address) {
        byte[] value = toByteArray(expandIpv6Address(address));

        return normalizeIpv6(value);
    }

    /**
     * A private method that takes 16 bytes of an IPv6 address and formats it as a normalized IPv6 address
     * 
     * @param segments
     *            The bytes that make up the address
     * @return The normalized IPV6 address string
     */
    private static String normalizeIpv6(byte[] segments) {
        StringBuffer address = new StringBuffer();
        boolean canSkip = true;
        boolean skipping = false;

        for (int i = 0; i < 16; i += 2) {
            int b = (int) ((256 + segments[i]) % 256 << 8 | (256 + segments[i + 1]) % 256);
            if (b == 0 && canSkip) {
                if (!skipping) {
                    address.append("::");
                    skipping = true;
                }
            } else {
                if (skipping) {
                    canSkip = false;
                }
                if (i != 0) {
                    address.append(':');
                }
                address.append(IPHelper.intToHexString(b));
            }
        }

        return address.toString();
    }

    /**
     * Expresses an ip address in the form of a CIDR (Classless Inter-Domain Routing) notation.
     * 
     * @param address
     *            The IP address to be converted to CIDR notation
     * @param prefixLength
     *            The prefix length
     * @return The equivalent CIDR notation
     */
    public static String toCIDR(String address, int prefixLength) {
        byte[] value = toByteArray(address);
        boolean ipv4 = isValidIpv4(address);
        if ((ipv4 && prefixLength > 31) || (!ipv4 && prefixLength > 63) || prefixLength < 1) {
            return "";
        }

        byte[] mask = new byte[value.length];
        for (int position = 0; position < prefixLength; position++) {
            int byteIndex = position / 8;
            int bitPosition = 7 - (position % 8);
            mask[byteIndex] = (byte) (mask[byteIndex] | 1 << bitPosition);
        }
        for (int index = 0; index < value.length; index++) {
            value[index] = (byte) (value[index] & mask[index]);
        }
        StringBuffer cidr = new StringBuffer();
        if (ipv4) {
            for (int i = 0; i < 4; i++) {
                cidr.append(Integer.toString((256 + value[i]) % 256));
                cidr.append('.');
            }
            cidr.delete(cidr.length() - 1, cidr.length());
        } else {
            cidr.append(normalizeIpv6(value));
            // for (int i = 0; i < 16; i += 2) {
            // cidr.append(IPHelper.intToHexString((256 + value[i]) % 256));
            // cidr.append(IPHelper.intToHexString((256 + value[i + 1]) % 256));
            // cidr.append(':');
            // }
            // cidr.delete(cidr.length() - 1, cidr.length());
        }
        cidr.append("/");
        cidr.append(Integer.toString(prefixLength));

        return cidr.toString();
    }

    /**
     * Converts an IP address (either IPv4 or IPv6) to an equivalent byte array
     * 
     * @param address
     *            The address to be converted
     * @return The value expressed as a byte array
     */
    public static byte[] toByteArray(String address) {
        if (isValidIpv4(address)) {
            return toByteArrayV4(address);
        } else if (isValidIpv6(address)) {
            return toByteArrayV6(address);
        }

        return null;
    }

    /**
     * @param address
     * @return value
     */
    private static byte[] toByteArrayV4(String address) {
        String[] tokens = address.split("\\.");
        byte[] value = new byte[4];
        for (int index = 0; index < 4; index++) {
            value[index] = (byte) Integer.parseInt(tokens[index]);
        }
        return value;
    }

    /**
     * @param address
     * @return value
     */
    private static byte[] toByteArrayV6(String address) {
        String[] tokens = address.split(":");
        byte[] value = new byte[16];
        for (int index = 0; index < value.length; index++) {
            value[index] = 0;
        }

        boolean emptyGroup = false;
        for (int srcIndex = 0, destIndex = 0; destIndex < value.length; srcIndex++, destIndex += 2) {
            if (tokens[srcIndex].length() == 0) {
                emptyGroup = true;
                break;
            }
            int num = hexStringToInt(tokens[srcIndex]);
            value[destIndex] = (byte) (num / 256);
            value[destIndex + 1] = (byte) (num % 256);
        }

        if (emptyGroup) {
            for (int srcIndex = tokens.length - 1, destIndex = 15; destIndex >= 0; srcIndex--, destIndex -= 2) {
                if (tokens[srcIndex].length() == 0) {
                    break;
                }
                int num = hexStringToInt(tokens[srcIndex]);
                value[destIndex] = (byte) (num % 256);
                value[destIndex - 1] = (byte) (num / 256);
            }
        }
        return value;
    }

    /**
     * A static array of the hexadecimal digits where their position represents their ordinal value
     */
    private static final char[] HEX_DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Converts a hexadecimal string value to the equivalent binary value. Cannot handle more than 8 hex digits.
     * 
     * @param value
     *            The value, expressed as a string of hexadecimal digits (0-F) that are to be converted
     * @return The equivalent binary value
     */
    public static int hexStringToInt(String value) {
        int result = 0;
        for (int position = 0, index = value.length() - 1; index >= 0; index--, position++) {
            char ch = Character.toUpperCase(value.charAt(index));
            for (int i = 0; i < HEX_DIGITS.length; i++) {
                if (HEX_DIGITS[i] == ch) {
                    result += (i * Math.pow(16, position));
                }
            }
        }
        return result;
    }

    /**
     * Convert an integer value into a hexadecimal representation that is long enough to contain the significant digits.
     * 
     * @param value
     *            The integer value to be converted
     * @return The string representation of the value as a hexadecimal representation
     */
    public static String intToHexString(int value) {
        StringBuffer buffer = new StringBuffer();
        int temp = value;
        do {
            int index = temp % 16;
            buffer.insert(0, HEX_DIGITS[index]);
            temp = temp / 16;
        } while (temp > 0);
        return buffer.toString();
    }
}
