package com.gsma.mobileconnect.r2.android.main;

import com.gsma.mobileconnect.r2.android.bus.BusManager;

import com.gsma.mobileconnect.r2.MobileConnectInterface;
import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.json.DiscoveryResponseData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class MobileConnectAndroidPresenterTest
{
    private IMobileConnectContract.IUserActionsListener presenter;

    @Mock
    private MobileConnectInterface mockMobileConnectInterface;

    @Mock
    private IMobileConnectContract.IView mockView;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        presenter = new MobileConnectAndroidPresenter(mockMobileConnectInterface);
        presenter.setView(mockView);
    }

    @Test
    public void setView() throws Exception
    {

    }

    @Test
    public void getDiscoveryResponse() throws Exception
    {

    }

    @Test
    public void setDiscoveryResponse() throws Exception
    {

    }

    @Test
    public void initialise() throws Exception
    {

    }

    @Test
    public void cleanUp() throws Exception
    {

    }

    @Test
    public void onEvent() throws Exception
    {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        DiscoveryResponse.Builder builder = new DiscoveryResponse.Builder();
        builder.withResponseData(new DiscoveryResponseData.Builder().build());
        final DiscoveryResponse discoveryResponse = builder.build();

        presenter.initialise();

        //When
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                BusManager.post(discoveryResponse);
                countDownLatch.countDown();
            }
        }).start();

        //Then
        countDownLatch.await();
        Assert.assertNotNull(presenter.getDiscoveryResponse());
        presenter.cleanUp();
    }

    @Test
    public void testInitialise()
    {
        presenter.setView(mockView);
        presenter.initialise();
        try
        {
            BusManager.register(presenter);
            Assert.fail("Expected an IllegalArgumentException to be thrown");
        }
        catch (IllegalArgumentException e)
        {
            presenter.cleanUp();
        }
    }

    @Test
    public void testPerformDiscovery() throws Exception
    {
        //Given
        String msisdn = "msisdn";
        String mcc = "mcc";
        String mnc = "mnc";
        MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder().build();
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);
        //When
        this.presenter.performDiscovery(msisdn, mcc, mnc, options, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformDiscoveryAfterOperatorSelection() throws Exception
    {
        //Given
        URI redirectUri = new URI("uri");
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performDiscoveryAfterOperatorSelection(mockIMobileConnectCallback, redirectUri);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
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
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performAuthentication(encryptedMSISDN,
                                             expectedState,
                                             expectedNonce,
                                             options,
                                             mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
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
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRequestToken(redirectUri,
                                           expectedState,
                                           expectedNonce,
                                           options,
                                           mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
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
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performHandleUrlRedirect(redirectUri,
                                                expectedState,
                                                expectedNonce,
                                                options,
                                                mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRequestIdentity() throws Exception
    {
        //Given
        String accessToken = "accessToken";
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRequestIdentity(accessToken, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRefreshToken() throws Exception
    {
        //Given
        String accessToken = "accessToken";
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRefreshToken(accessToken, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRevokeToken() throws Exception
    {
        //Given
        String token = "token";
        String tokenTypeHint = "tokenTypeHint";
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRevokeToken(token, tokenTypeHint, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }

    @Test
    public void testPerformRequestUserInfo() throws Exception
    {
        //Given
        String accessToken = "accessToken";
        IMobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(IMobileConnectContract
                                                                                                       .IMobileConnectCallback.class);

        //When
        this.presenter.performRequestUserInfo(accessToken, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView)
               .performAsyncTask(any(IMobileConnectContract.IMobileConnectOperation.class),
                                 eq(mockIMobileConnectCallback));
    }
}