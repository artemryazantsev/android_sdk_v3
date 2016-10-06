/**
 * Created by usmaan.dad on 29/09/2016.
 */
public class MobileConnectAndroidInterfaceTest
{
//    @Test
//    public void attemptDiscoveryWithoutMSISDN()
//    {
//        URI discoveryUri = null;
//        try
//        {
//            discoveryUri = new URI("https://reference.mobileconnect.io/discovery/");
//        }
//        catch (URISyntaxException e)
//        {
//            e.printStackTrace();
//        }
//
//        URI redirectUri = null;
//        try
//        {
//            redirectUri = new URI("https://reference.mobileconnect.io/discovery/");
//        }
//        catch (URISyntaxException e)
//        {
//            e.printStackTrace();
//        }
//
//        MobileConnectConfig mobileConnectConfig = new MobileConnectConfig.Builder().withClientId("ZWRhNjU3OWI3MGIwYTRh")
//                                                                                   .withClientSecret(
//                                                                                           "ZWRhNjU3OWI3MGIwYTRh")
//                                                                                   .withDiscoveryUrl(discoveryUri)
//                                                                                   .withRedirectUrl(redirectUri)
//                                                                                   .withCacheResponsesWithSessionId(
//                                                                                           false)
//                                                                                   .build();
//
//        final MobileConnect mobileConnect = MobileConnect.build(mobileConnectConfig,
//                                                                new AndroidMobileConnectEncodeDecoder());
//
//        MobileConnectInterface mobileConnectInterface = mobileConnect.getMobileConnectInterface();
//
//        MobileConnectAndroidInterface mobileConnectAndroidInterface = new MobileConnectAndroidInterface(
//                mobileConnectInterface);
//
//        DiscoveryOptions.Builder discoveryOptionsBuilder = new DiscoveryOptions.Builder();
//
//
//        MobileConnectRequestOptions requestOptions = new MobileConnectRequestOptions.Builder().withDiscoveryOptions(
//                discoveryOptionsBuilder.withMsisdn(null).build()).build();
//
//
//        mobileConnectAndroidInterface.attemptDiscovery(null,
//                                                       null,
//                                                       null,
//                                                       requestOptions,
//                                                       new MobileConnectAndroidInterface.IMobileConnectCallback()
//                                                       {
//                                                           @Override
//                                                           public void onComplete(MobileConnectStatus
//                                                                                          mobileConnectStatus)
//                                                           {
//                                                               assertThat(mobileConnectStatus, notNullValue());
//                                                           }
//                                                       });
//    }
}
