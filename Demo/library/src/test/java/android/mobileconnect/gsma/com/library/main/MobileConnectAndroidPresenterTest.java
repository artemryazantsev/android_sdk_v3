package android.mobileconnect.gsma.com.library.main;

import com.gsma.mobileconnect.r2.MobileConnectInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

/**
 * Created by kartik.prabhu on 10/10/2016.
 */
public class MobileConnectAndroidPresenterTest {

    private MobileConnectContract.UserActionsListener presenter;

    @Mock
    private MobileConnectInterface mockMobileConnectInterface;

    @Mock
    private MobileConnectContract.View mockView;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        presenter = new MobileConnectAndroidPresenter(mockMobileConnectInterface);
        presenter.setView(mockView);
    }

    @Test
    public void performDiscovery() throws Exception {

        //Given
        MobileConnectContract.IMobileConnectCallback mockIMobileConnectCallback = Mockito.mock(MobileConnectContract.IMobileConnectCallback.class);

        //When
        this.presenter.performDiscovery("msisdn", "mcc", "mnc", null, mockIMobileConnectCallback);

        //Then
        Mockito.verify(mockView).performAsyncTask(Matchers.any(MobileConnectContract.IMobileConnectOperation.class), mockIMobileConnectCallback);
        assertNotNull(this.presenter.getDiscoveryResponse());
    }

}