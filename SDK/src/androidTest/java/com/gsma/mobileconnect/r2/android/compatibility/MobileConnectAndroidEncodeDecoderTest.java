package com.gsma.mobileconnect.r2.android.compatibility;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MobileConnectAndroidEncodeDecoderTest
{
    @Test
    public void testDecodeFromBase64() throws Exception
    {
        // Given
        AndroidMobileConnectEncodeDecoder androidEncodeDecoder = new AndroidMobileConnectEncodeDecoder();
        String actualEncodedBytes =
                "eyJub25jZSI6IjY1MjgxYjRkLWM0MmItNDBmZi1iNDBlLWFhYjQ3MDNhZDY1OSIsImFtciI6WyJTSU1fT0siXSwiYXpwIjoiTXpGbFpqa3haR0l0T1dVMk5TMDBaVEZtTFRrd016Y3ROVFF6Tmpka01EQmtNemN6T205d1pYSmhkRzl5TFdjPSIsInN1YiI6IjkxNzI5NTYyLTkxNGUtMTFlNi1iMGQ0LTAyNDJhYzExMDAwMyIsImV4cCI6MTQ3NjM3NDA5NSwiYXV0aF90aW1lIjoxNDc2Mzc0MDg1LjAsImlzcyI6Imh0dHBzOi8vb3BlcmF0b3ItZy5pbnRlZ3JhdGlvbi5zYW5kYm94Lm1vYmlsZWNvbm5lY3QuaW8iLCJhdWQiOlsiTXpGbFpqa3haR0l0T1dVMk5TMDBaVEZtTFRrd016Y3ROVFF6Tmpka01EQmtNemN6T205d1pYSmhkRzl5TFdjPSJdLCJhY3IiOiIyIiwiaWF0IjoxNDc2Mzc0MDg1LCJkaXNwbGF5ZWRfZGF0YSI6ImNzLXNkay1kZW1vIGF1dGgtZGVtbyJ9";

        // When
        byte[] decodedBytes = androidEncodeDecoder.decodeFromBase64(actualEncodedBytes);

        // Then
        assertNotNull(decodedBytes);
    }

    @Test
    public void testEncodeFromBase64() throws Exception
    {
        // Given
        AndroidMobileConnectEncodeDecoder androidEncodeDecoder = new AndroidMobileConnectEncodeDecoder();
        String actualEncodedBytes =
                "eyJub25jZSI6IjY1MjgxYjRkLWM0MmItNDBmZi1iNDBlLWFhYjQ3MDNhZDY1OSIsImFtciI6WyJTSU1fT0siXSwiYXpwIjoiTXpGbFpqa3haR0l0T1dVMk5TMDBaVEZtTFRrd016Y3ROVFF6Tmpka01EQmtNemN6T205d1pYSmhkRzl5TFdjPSIsInN1YiI6IjkxNzI5NTYyLTkxNGUtMTFlNi1iMGQ0LTAyNDJhYzExMDAwMyIsImV4cCI6MTQ3NjM3NDA5NSwiYXV0aF90aW1lIjoxNDc2Mzc0MDg1LjAsImlzcyI6Imh0dHBzOi8vb3BlcmF0b3ItZy5pbnRlZ3JhdGlvbi5zYW5kYm94Lm1vYmlsZWNvbm5lY3QuaW8iLCJhdWQiOlsiTXpGbFpqa3haR0l0T1dVMk5TMDBaVEZtTFRrd016Y3ROVFF6Tmpka01EQmtNemN6T205d1pYSmhkRzl5TFdjPSJdLCJhY3IiOiIyIiwiaWF0IjoxNDc2Mzc0MDg1LCJkaXNwbGF5ZWRfZGF0YSI6ImNzLXNkay1kZW1vIGF1dGgtZGVtbyJ9";

        byte[] decodedBytes = androidEncodeDecoder.decodeFromBase64(actualEncodedBytes);

        // When
        String encoded = androidEncodeDecoder.encodeToBase64(decodedBytes);

        // Then
        assertNotNull(encoded);
        assertEquals(encoded, actualEncodedBytes);
    }
}