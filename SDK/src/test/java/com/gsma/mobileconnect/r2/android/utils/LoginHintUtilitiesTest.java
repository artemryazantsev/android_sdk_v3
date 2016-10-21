package com.gsma.mobileconnect.r2.android.utils;

import com.gsma.mobileconnect.r2.constants.LoginHintPrefixes;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class LoginHintUtilitiesTest
{
    @Test
    public void isSupportedMSISDNShouldReturnTrueIfMSISDNIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder().withLoginHintMethodsSupported(new ArrayList<String>()
        {{
            add(LoginHintPrefixes.MSISDN.getName());
        }}).build();

        final boolean supportedForMsisdn = LoginHintUtilities.isSupportedForMsisdn(providerMetadata);

        assertTrue(supportedForMsisdn);
    }

    @Test
    public void isSupportedMSISDNShouldReturnFalseIfMSISDNNotIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder().withLoginHintMethodsSupported(new ArrayList<String>()
        {{
            add(LoginHintPrefixes.PCR.getName());
        }}).build();

        final boolean supportedForMsisdn = LoginHintUtilities.isSupportedForMsisdn(providerMetadata);

        assertFalse(supportedForMsisdn);
    }

    @Test
    public void isSupportedEncryptedMSISDNShouldReturnTrueIfEncryptedMSISDNIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder().withLoginHintMethodsSupported(new ArrayList<String>()
        {{
            add(LoginHintPrefixes.ENCRYPTED_MSISDN.getName());
        }}).build();

        final boolean supportedForEncryptedMsisdn = LoginHintUtilities.isSupportedForEncryptedMsisdn(providerMetadata);

        assertTrue(supportedForEncryptedMsisdn);
    }

    @Test
    public void isSupportedEncryptedMSISDNShouldReturnFalseIfEncryptedMSISDNNotIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder().withLoginHintMethodsSupported(new ArrayList<String>()
        {{
            add(LoginHintPrefixes.PCR.getName());
        }}).build();

        final boolean supportedForEncryptedMsisdn = LoginHintUtilities.isSupportedForEncryptedMsisdn(providerMetadata);

        assertFalse(supportedForEncryptedMsisdn);
    }

    @Test
    public void isSupportedPCRShouldReturnTrueIfPCRIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder().withLoginHintMethodsSupported(new ArrayList<String>()
        {{
            add(LoginHintPrefixes.PCR.getName());
        }}).build();

        final boolean supportedForPcr = LoginHintUtilities.isSupportedForPcr(providerMetadata);

        assertTrue(supportedForPcr);
    }

    @Test
    public void isSupportedPCRShouldReturnFalseIfPCRNotIncluded()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder().withLoginHintMethodsSupported(new ArrayList<String>()
        {{
            add(LoginHintPrefixes.MSISDN.getName());
        }}).build();

        final boolean supportedForPcr = LoginHintUtilities.isSupportedForPcr(providerMetadata);

        assertFalse(supportedForPcr);
    }

    @Test
    public void isSupportedMSISDNShouldReturnTrueIfMissingMetadata()
    {
        final boolean supportedForMsisdn = LoginHintUtilities.isSupportedForMsisdn(null);

        assertTrue(supportedForMsisdn);
    }

    @Test
    public void isSupportedEncryptedMSISDNShouldReturnTrueIfMissingMetadata()
    {
        final boolean supportedForEncryptedMsisdn = LoginHintUtilities.isSupportedForEncryptedMsisdn(null);

        assertTrue(supportedForEncryptedMsisdn);
    }

    @Test
    public void isSupportedPCRShouldReturnTrueIfMissingMetadata()
    {
        final boolean supportedForPcr = LoginHintUtilities.isSupportedForPcr(null);

        assertTrue(supportedForPcr);
    }

    @Test
    public void isSupportedPCRShouldReturnTrueIfSupportedVersionIs1_2()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder().withMobileConnectVersionSupported
                (new SupportedVersions.Builder()
                                                                                                                           .addSupportedVersion(
                                                                                                                                   Scopes.MOBILECONNECT,
                                                                                                                                   "mc_v1.2")
                                                                                                                           .build())
                                                                                .withLoginHintMethodsSupported(null)
                                                                                .build();

        final boolean supportedForPcr = LoginHintUtilities.isSupportedForPcr(providerMetadata);

        assertTrue(supportedForPcr);
    }

    @Test
    public void isSupportedForShouldReturnFalseIfUnrecognisedPrefixAndMissingMetadata()
    {
        final boolean supportedFor = LoginHintUtilities.isSupportedFor(null, "testprefix");

        assertFalse(supportedFor);
    }

    @Test
    public void isSupportedForShouldBeCaseInsensitive()
    {
        final ProviderMetadata providerMetadata = new ProviderMetadata.Builder().withLoginHintMethodsSupported(new ArrayList<String>()
        {{
            add(LoginHintPrefixes.MSISDN.getName());
        }}).build();

        final boolean supportedForMsisdn = LoginHintUtilities.isSupportedFor(providerMetadata, "msiSDN");

        assertTrue(supportedForMsisdn);
    }

    @Test
    public void generateForMSISDNShouldGenerateCorrectFormat()
    {
        final String msisdnLoginHint = LoginHintUtilities.generateForMsisdn("+447700900250");

        assertEquals(msisdnLoginHint, "MSISDN:447700900250");
    }

    @Test
    public void generateForEncryptedMSISDNShouldGenerateCorrectFormat()
    {
        final String encryptedMsisdnLoginHint = LoginHintUtilities.generateForEncryptedMsisdn("zmalqpwoeirutyfhdjskaslxzmxncbv");

        assertEquals(encryptedMsisdnLoginHint, "ENCR_MSISDN:zmalqpwoeirutyfhdjskaslxzmxncbv");
    }

    @Test
    public void generateForPCRShouldGenerateCorrectFormat()
    {
        final String pcrLoginHint = LoginHintUtilities.generateForPcr("zmalqpwoeirutyfhdjskaslxzmxncbv");

        assertEquals(pcrLoginHint, "PCR:zmalqpwoeirutyfhdjskaslxzmxncbv");
    }

    @Test
    public void generateForShouldReturnNullWhenValueNull()
    {
        assertNull(LoginHintUtilities.generateFor(LoginHintPrefixes.PCR.getName(), null));
    }

    @Test
    public void generateForShouldReturnNullWhenValueEmpty()
    {
        assertNull(LoginHintUtilities.generateFor(LoginHintPrefixes.PCR.getName(), ""));
    }

    @Test
    public void generateForShouldReturnNullWhenPrefixNull()
    {
        assertNull(LoginHintUtilities.generateFor(null, "testValue"));
    }

    @Test
    public void generateForShouldReturnNullWhenPrefixEmpty()
    {
        assertNull(LoginHintUtilities.generateFor("", "testValue"));
    }
}