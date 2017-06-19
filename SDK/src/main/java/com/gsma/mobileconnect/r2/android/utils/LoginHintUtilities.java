package com.gsma.mobileconnect.r2.android.utils;

import com.gsma.mobileconnect.r2.authentication.LoginHint;
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;

/**
 * Utility methods for working with login hints for the auth login hint parameter
 *
 * @since 2.0
 */
public class LoginHintUtilities
{
    private LoginHintUtilities()
    {
        /*
        Empty Private Constructor since all methods are static
         */
    }

    /**
     * Is login hint with MSISDN supported by the target provider
     *
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format MSISDN:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForMsisdn(final ProviderMetadata providerMetadata)
    {
        return LoginHint.isSupportedForMsisdn(providerMetadata);
    }

    /**
     * Is login hint with Encrypted MSISDN (SubscriberId) supported by the target provider
     *
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format ENCRYPTED_MSISDN:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForEncryptedMsisdn(final ProviderMetadata providerMetadata)
    {
        return LoginHint.isSupportedForEncryptedMsisdn(providerMetadata);
    }

    /**
     * Is login hint with PCR (Pseudonymous Customer Reference) supported by the target provider
     *
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format PCR:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForPcr(final ProviderMetadata providerMetadata)
    {
        return LoginHint.isSupportedForPcr(providerMetadata);
    }

    /**
     * Is login hint with specified prefix supported by the target provider
     *
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @param prefix           Prefix to check for login hint support
     * @return True if format ${prefix}:xxxxxxxxxx is supported
     */
    public static boolean isSupportedFor(final ProviderMetadata providerMetadata, final String prefix)
    {
        return LoginHint.isSupportedFor(providerMetadata, prefix);
    }

    /**
     * Generates login hint for MSISDN value
     *
     * @param msisdn MSISDN value
     * @return Correctly formatted login hint parameter for MSISDN
     */
    public static String generateForMsisdn(final String msisdn)
    {
        return LoginHint.generateForMsisdn(msisdn);
    }

    /**
     * Generates login hint for Encrypted MSISDN (SubscriberId) value
     *
     * @param encryptedMsisdn Encrypted MSISDN value
     * @return Correctly formatted login hint parameter for Encrypted MSISDN
     */
    public static String generateForEncryptedMsisdn(final String encryptedMsisdn)
    {
        return LoginHint.generateForEncryptedMsisdn(encryptedMsisdn);
    }

    /**
     * Generates login hint for PCR (Pseudonymous Customer Reference) value
     *
     * @param pcr PCR (Pseudonymous Customer Reference)
     * @return Correctly formatted login hint parameter for PCR (Pseudonymous Customer Reference)
     */
    public static String generateForPcr(final String pcr)
    {
        return LoginHint.generateForPcr(pcr);
    }

    /**
     * Generates a login hint for the specified prefix with the specified value.
     * This method will not check that the prefix is recognised or supported, it is assumed that it is supported.
     *
     * @param prefix Prefix to use
     * @param value  Value to use
     * @return Correctly formatted login hint for prefix and value
     */
    public static String generateFor(final String prefix, final String value)
    {
        return LoginHint.generateFor(prefix, value);
    }
}