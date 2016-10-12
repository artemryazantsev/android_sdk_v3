package android.mobileconnect.gsma.com.library.main;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class MobileConnectAndroidPresenterTest
{
    private MobileConnectContract.UserActionsListener presenter;

    @Mock
    private MobileConnectInterface mockMobileConnectInterface;

    @Mock
    private MobileConnectContract.View mockView;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        presenter = new MobileConnectAndroidPresenter(mockMobileConnectInterface);
        presenter.setView(mockView);
    }

    @Test
    public void testPerformDiscovery() throws Exception
    {
        //Given
        String msisdn = "msisdn";
        String mcc = "mcc";
        String mnc = "mnc";
        MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder().build();
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);
        //When
        this.presenter.performDiscovery(msisdn, mcc, mnc, options, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformDiscoveryAfterOperatorSelection() throws Exception
    {
        //Given
        URI redirectUri = new URI("uri");
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performDiscoveryAfterOperatorSelection(mockIMobileConnectCallback, redirectUri);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformAuthentication() throws Exception
    {
        //Given
        String encryptedMSISDN = "msisdn";
        String expectedNonce = "expectedNonce";
        String expectedState = "expectedState";
        MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder().build();
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performAuthentication(encryptedMSISDN,
                                             expectedState,
                                             expectedNonce,
                                             options,
                                             mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRequestToken() throws Exception
    {
        //Given
        URI redirectUri = new URI("uri");
        String expectedNonce = "expectedNonce";
        String expectedState = "expectedState";
        MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder().build();
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRequestToken(redirectUri,
                                           expectedState,
                                           expectedNonce,
                                           mockIMobileConnectCallback,
                                           options);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformHandleUrlRedirect() throws Exception
    {
        //Given
        URI redirectUri = new URI("uri");
        String expectedNonce = "expectedNonce";
        String expectedState = "expectedState";
        MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder().build();
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performHandleUrlRedirect(redirectUri,
                                                expectedState,
                                                expectedNonce,
                                                mockIMobileConnectCallback,
                                                options);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRequestIdentity() throws Exception
    {
        //Given
        String accessToken = "accessToken";
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRequestIdentity(accessToken, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRefreshToken() throws Exception
    {
        //Given
        String accessToken = "accessToken";
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRefreshToken(accessToken, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRevokeToken() throws Exception
    {
        //Given
        String token = "token";
        String tokenTypeHint = "tokenTypeHint";
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRevokeToken(token, tokenTypeHint, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRequestUserInfo() throws Exception
    {
        //Given
        String accessToken = "accessToken";
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRequestUserInfo(accessToken, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(MobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }
}