package android.mobileconnect.gsma.com.library.compatibility;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by usmaan.dad on 12/10/2016.
 */
@RunWith(AndroidJUnit4.class)
public class MobileConnectAndroidEncodeDecoderTest
{
    @Test
    public void encodeToBase64() throws Exception
    {
        //Given
        AndroidMobileConnectEncodeDecoder androidEncodeDecoder = new AndroidMobileConnectEncodeDecoder();
        byte[] bytes = new byte[]{3, 4, 5, 55, 66};
        String actualEncodedBytes = "AwQFN0I=";

        //When
        String encodedBytes = androidEncodeDecoder.encodeToBase64(bytes);

        //Then
        assertEquals(encodedBytes, actualEncodedBytes);
    }

    @Test
    public void decodeFromBase64() throws Exception
    {
        //Given
        AndroidMobileConnectEncodeDecoder androidEncodeDecoder = new AndroidMobileConnectEncodeDecoder();
        byte[] bytes = new byte[]{3, 4, 5, 55, 66};
        String actualEncodedBytes = "AwQFN0I=";

        //When
        byte[] decodedBytes = androidEncodeDecoder.decodeFromBase64(actualEncodedBytes);

        //Then
        assertThat(bytes, equalTo(decodedBytes));
    }
}