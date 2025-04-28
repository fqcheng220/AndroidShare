package com.fqcheng220.android.androidshare.base.android;

import android.content.Context;
import android.os.SystemClock;

import com.fqcheng220.android.androidshare.utils.Logger;
import com.fqcheng220.android.androidshare.utils.SharedPreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description: (用一句话描述该文件做什么)
 * @date 2025/4/27 13:08
 */
public class SharedPreferenceDemo {

    public static void test(Context context) {
        SharedPreferenceDemo sharedPreferenceDemo = new SharedPreferenceDemo();
        int i = 0;
        while (i++ < 200) {
            sharedPreferenceDemo.testOnceSingleThreadSyncOper(context, String.valueOf(i));
            SystemClock.sleep(50);
        }
    }

    /**
     * 可以明确的是async写之后立刻读是可以读到最新的内容的
     * @param context
     * @param threadTag
     */
    void testOnceSingleThreadAsyncOper(Context context, final String threadTag) {
        Logger.d("SharedPreferenceDemo", "testOnceSingleThreadAsyncOper");
        final String spName = "spNameTest";
        final String spKey = "spKeyTest";
        SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_INIT);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 async write start", threadTag));
                SharedPreferenceUtils.asyncPutString(context, spName, spKey, VALUE_AFTER);
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 async write end", threadTag));
                String value = SharedPreferenceUtils.getString(context, spName, spKey);
                String monitorVal = "";
                try {
                    JSONObject jsonObject = new JSONObject(value).optJSONObject("trade_url_info");
                    monitorVal = jsonObject.optString("quoteswitchurl-1");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1  read end %s", threadTag, monitorVal));
            }
        });
        thread1.start();
    }

    void testOnceSingleThreadSyncOper(Context context, final String threadTag) {
        Logger.d("SharedPreferenceDemo", "testOnceSingleThreadSyncOper");
        final String spName = "spNameTest";
        final String spKey = "spKeyTest";
        SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_INIT);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 sync write start", threadTag));
                SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_AFTER);
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 sync write end", threadTag));
                String value = SharedPreferenceUtils.getString(context, spName, spKey);
                String monitorVal = "";
                try {
                    JSONObject jsonObject = new JSONObject(value).optJSONObject("trade_url_info");
                    monitorVal = jsonObject.optString("quoteswitchurl-1");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1  read end %s", threadTag, monitorVal));
            }
        });
        thread1.start();
    }

    void testOnceMultiThreadAsyncOperNotInOrder(Context context, final String threadTag) {
        Logger.d("SharedPreferenceDemo", "testOnceMultiThreadAsyncOperNotInOrder");
        final String spName = "spNameTest";
        final String spKey = "spKeyTest";
        SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_INIT);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 async write start", threadTag));
                SharedPreferenceUtils.asyncPutString(context, spName, spKey, VALUE_AFTER);
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 async write end", threadTag));
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread2  read start", threadTag));
                String value = SharedPreferenceUtils.getString(context, spName, spKey);
                String monitorVal = "";
                try {
                    JSONObject jsonObject = new JSONObject(value).optJSONObject("trade_url_info");
                    monitorVal = jsonObject.optString("quoteswitchurl-1");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread2  read end %s", threadTag, monitorVal));
            }
        });
        thread1.start();
        thread2.start();
    }

    void testOnceMultiThreadSyncOperNotInOrder(Context context, final String threadTag) {
        Logger.d("SharedPreferenceDemo", "testOnceMultiThreadSyncOperNotInOrder");
        final String spName = "spNameTest";
        final String spKey = "spKeyTest";
        SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_INIT);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 sync write start", threadTag));
                SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_AFTER);
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 sync write end", threadTag));
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread2  read start", threadTag));
                String value = SharedPreferenceUtils.getString(context, spName, spKey);
                String monitorVal = "";
                try {
                    JSONObject jsonObject = new JSONObject(value).optJSONObject("trade_url_info");
                    monitorVal = jsonObject.optString("quoteswitchurl-1");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread2  read end %s", threadTag, monitorVal));
            }
        });
        thread1.start();
        thread2.start();
    }

    void testOnceMultiThreadAsyncOperInOrder(Context context, final String threadTag) {
        Logger.d("SharedPreferenceDemo", "testOnceMultiThreadAsyncOperInOrder");
        final String spName = "spNameTest";
        final String spKey = "spKeyTest";
        SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_INIT);
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread2  read start", threadTag));
                String value = SharedPreferenceUtils.getString(context, spName, spKey);
                String monitorVal = "";
                try {
                    JSONObject jsonObject = new JSONObject(value).optJSONObject("trade_url_info");
                    monitorVal = jsonObject.optString("quoteswitchurl-1");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread2  read end %s", threadTag, monitorVal));
            }
        });
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 async write start", threadTag));
                SharedPreferenceUtils.asyncPutString(context, spName, spKey, VALUE_AFTER);
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 async write end", threadTag));
                thread2.start();
            }
        });
        thread1.start();
    }

    void testOnceMultiThreadSyncOperInOrder(Context context, final String threadTag) {
        Logger.d("SharedPreferenceDemo", "testOnceMultiThreadSyncOperInOrder");
        final String spName = "spNameTest";
        final String spKey = "spKeyTest";
        SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_INIT);
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread2  read start", threadTag));
                String value = SharedPreferenceUtils.getString(context, spName, spKey);
                String monitorVal = "";
                try {
                    JSONObject jsonObject = new JSONObject(value).optJSONObject("trade_url_info");
                    monitorVal = jsonObject.optString("quoteswitchurl-1");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread2  read end %s", threadTag, monitorVal));
            }
        });
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 sync write start", threadTag));
                SharedPreferenceUtils.syncPutString(context, spName, spKey, VALUE_AFTER);
                Logger.d("SharedPreferenceDemo", String.format("[%s]thread1 sync write end", threadTag));
                thread2.start();
            }
        });
        thread1.start();
    }

    public static final String VALUE_INIT = "{\n" + "  \"trade_base_info\": {\n" + "    \"quoteswitch\": \"1\",\n" + "    \"telNum\": \"95357\",\n" + "    \"REQUEST_MAX_TIME\": \"300\",\n" + "    \"version\": \"1.1\",\n" + "    \"codeonlineswitchhttp\": \"0\",\n" + "    \"app_kaihu_verify_switch\": \"1\",\n" + "    \"kaihu_huoti_switch\": \"1\",\n" + "    \"kaihu_huoti_step\": \"1\",\n" + "    \"quoteswitch_hk\": \"1\",\n" + "    \"quoteswitch_usa\": \"1\",\n" + "    \"kaihu_doublevideo_switch\": \"1\",\n" + "    \"kaihu_huoti_retry_step\": \"1\",\n" + "    \"trade_nosession_switch\": \"1\",\n" + "    \"trade_nosession_device_switch\": \"0\",\n" + "    \"app_gm_switch\": \"2\",\n" + "    \"app_trade_cancel_switch\": \"1\",\n" + "    \"app_todolist_switch\": \"1\",\n" + "    \"app_loginphone_auth_switch\": \"1\",\n" + "    \"app_trade_bjsbond_switch\": \"1\",\n" + "    \"kaihu_idcard_guide_switch\": \"1\",\n" + "    \"trade_batch_tradeorder_num\": \"40\",\n" + "    \"trade_batch_cancelorder_num\": \"40\",\n" + "    \"app_home_zcwf_1_switch\": \"1\",\n" + "    \"app_home_zcwf_2_switch\": \"1\",\n" + "    \"app_chigudays_switch\": \"1\",\n" + "    \"app_kaihu_hometab_switch\": \"1\",\n" + "    \"app_trade_tradenoconfirm_switch\": \"1\",\n" + "    \"app_home_assetcache_switch\": \"1\",\n" + "    \"app_home_assetcache_time\": \"24\",\n" + "    \"app_home_zcwf_3_switch\": \"1\",\n" + "    \"app_home_zcwf_4_switch\": \"1\",\n" + "    \"app_trade_costprice_switch\": \"1\",\n" + "    \"app_trade_loginphone_switch\": \"1\",\n" + "    \"app_trade_ipv6_disable_switch\": \"0\",\n" + "    \"app_trade_home_cornermark_switch\": \"0\",\n" + "    \"app_home_faceid_fingerprint_loopday\": \"14\",\n" + "    \"app_trade_ipo_switch\": \"1\",\n" + "    \"app_trade_bzstock_mapping_switch\": \"0\",\n" + "    \"trade_batch_tradeorder_view_num\": \"999\",\n" + "    \"app_trade_viplevel_switch\": \"0\",\n" + "    \"app_trade_homeviplevel_switch\": \"1\",\n" + "    \"app_trade_summary_deal_switch\": \"1\",\n" + "    \"trade_summary_deal_num\": \"5000\",\n" + "    \"trade_record_ggt_merge_switch\": \"1\",\n" + "    \"trade_modify_price_switch\": \"1\",\n" + "    \"app_trade_ipv6_connect_timeout\": \"5\"\n" + "  },\n" + "  \"trade_url_info\": {\n" + "    \"quoteswitchurl-1\": \"https://emhsmarketjqdxmix.18.cn/api/shszmisc/appquote/\",\n" + "    \"quoteswitchurl-2\": \"https://emhsmarketwgmix.eastmoneysec.com/api/shszmisc/appquote/\",\n" + "    \"quoteswitchhkurl-1\": \"https://hkmarketzp.eastmoney.com/api/hkmisc/appquote/\",\n" + "    \"quoteswitchhkurl-2\": \"https://hkmarket.eastmoney.com/api/hkmisc/appquote/\",\n" + "    \"trade_base\": \"https://mixh5tradejqdxmix.18.cn\",\n" + "    \"trade_base_resource\": \"https://xcwebresourcejq.securities.eastmoney.com\",\n" + "    \"trade_ad_url\": \"/Assets/GetAd_Notice?type=4&adCode=679\",\n" + "    \"trade_message_center_url\": \"https://emdcmessagemix.18.cn\",\n" + "    \"txTradeUrl\": \"https://simpletrade.18.cn/login/transit.html\",\n" + "    \"txTradeQuickBuyUrl\": \"https://simpletrade.18.cn/login/transit.html\",\n" + "    \"txTradeQuickSellUrl\": \"https://simpletrade.18.cn/login/transit.html\",\n" + "    \"GET_NOTICE_DATA_URL\": \"https://tradenoticemix.18.cn/api/NoticeApi/GetLayerNoticeList\",\n" + "    \"POST_NOTICE_DATA_URL\": \"https://tradenoticemix.18.cn/api/NoticeApi/SaveLayerNoticeClick\",\n" + "    \"TRADE_QRQM_ASSET_URL\": \"https://alyjqdxmix.18.cn/Analyze/GetAssets\",\n" + "    \"TRADE_QRQM_AD_URL\": \"https://emdcadvertise.eastmoney.com/infoService/v2\",\n" + "    \"trade_message_center_hint_url\": \"https://emdcmessagemix.18.cn\",\n" + "    \"TRADE_QRQM_ASSET_ANALYSIS_URL\": \"dfcft://quicktrade?tab_position=4\",\n" + "    \"trade_gateway_http_url\": \"https://tradegatewayjqdxmix.18.cn/\",\n" + "    \"app_setting_storage_url\": \"https://emdcdataconfig.eastmoney.com/configService\",\n" + "    \"trade_filesystem_url\": \"https://filesystemjqdxmix.18.cn:8081\",\n" + "    \"rnhisquoteswitchurl-1\": \"https://push2his.eastmoney.com\",\n" + "    \"rnquoteswitchurl-1\": \"https://emhsmarketjqdxmix.18.cn\",\n" + "    \"rnhkquoteswitchurl-1\": \"https://hkmarketzp.eastmoney.com\",\n" + "    \"trade_gateway_http_url_bak\": \"https://tradegatewayjqtxmix.18.cn/\",\n" + "    \"trade_hk_url\": \"https://hktrade.eastmoney.com/V65Hybrid\",\n" + "    \"trade_usa_url\": \"https://hktrade.eastmoney.com/V65Hybrid\",\n" + "    \"trade_hkApp_url\": \"https://acttg.eastmoney.com/pub/apptg_app_act_xzcjydj_01_01_01_0?_track=other.click&_trackid=2021012100002\",\n" + "    \"trade_hkAppipo_url\": \"https://acttg.eastmoney.com/pub/apptg_app_act_zbxgcjy_01_01_01_0?_track=other.click&_trackid=2021012100001\",\n" + "    \"trade_home_subtitle\": \"https://emdcadvertise.eastmoney.com/infoService/trade\",\n" + "    \"newrnquoteswitchurl-1\": \"https://emhsmarketjqdxmix.18.cn\",\n" + "    \"trade_rnweb_url\": \"https://emrntradexcjqmix.18.cn\",\n" + "    \"trade_condition_url\": \"https://emdctrade.eastmoneysec.com/api/user/conditionV2\"\n" + "  },\n" + "  \"tradelogin\": {\n" + "    \"trade_czjymm\": \"https://openappjqdxmix.18.cn/Password?from=native\",\n" + "    \"trade_webrukou\": \"https://h5tradejqdxmix.18.cn/LogIn/Transit\",\n" + "    \"chat_on_line\": \"dfcft://emrn?id=znkf&page=Guide\",\n" + "    \"cjwt_url\": \"dfcft://emrn?id=HelpCenter\",\n" + "    \"yjsl_url\": \"dfcft://router/launcher/feedBack?feedBackId=327680\",\n" + "    \"feature_login_phone_auth\": \"0\",\n" + "    \"zhyty_url\": \"dfcft://emrn?id=Unregister&page=ActiveAccount&title=%E9%87%8D%E5%90%AF%E8%B5%84%E9%87%91%E8%B4%A6%E5%8F%B7\"\n" + "  },\n" + "  \"AStock_Account_Info\": {\n" + "    \"Account_Api_Url\": \"https://khapijqdxmix.18.cn\",\n" + "    \"AnychatIp\": \"videoauthwgdx.18.cn\",\n" + "    \"AnychatPort\": \"8971\",\n" + "    \"Kh_FirstStep_Config_Url\": \"\",\n" + "    \"Kh_LastStep_Config_Url\": \"\",\n" + "    \"Kaihu_h5_Url\": \"https://openappjqdxmix.18.cn/Regist/GotoAccountResult\",\n" + "    \"video_min_time\": \"7\",\n" + "    \"video_max_time\": \"25\",\n" + "    \"kaihu_hometab_url\": \"https://marketing.dfcfs.cn/views/tradekh/index\",\n" + "    \"kaihu_hometab_skin_w\": \"#FF3F06\",\n" + "    \"kaihu_hometab_skin_b\": \"#1C1D1E\",\n" + "    \"rn_openacc_url\": \"https://openappjqdxmix.18.cn/?start=native&ad_id=20040_2_4\",\n" + "    \"video_voice_callback_url\": \"https://zqsjjkmix.18.cn\"\n" + "  },\n" + "  \"Langke_Account_Info\": {\n" + "    \"Account_Api_Url\": \"https://khapijqdxmix.18.cn/\",\n" + "    \"lkaddr\": \"lkkhsrvmix.18.cn\",\n" + "    \"lkport\": \"443\",\n" + "    \"lkprotocol\": \"s\",\n" + "    \"Kaihu_h5_Url\": \"https://openappjqdxmix.18.cn/Regist/GotoAccountResult\",\n" + "    \"video_min_time\": \"7\",\n" + "    \"video_max_time\": \"30\",\n" + "    \"lklowerlimit\": \"0.3\",\n" + "    \"volumeAdviceLevel\": \"0.6\",\n" + "    \"volumeLowerLevel\": \"0.2\",\n" + "    \"tooFarFaceRatio\": \"0.4\",\n" + "    \"tooCloseFaceRatio\": \"0.85\",\n" + "    \"photoBlurValue\": \"6.0\",\n" + "    \"brightnessValue\": \"-2\",\n" + "    \"lkvpnaddr\": \"106.14.35.180\",\n" + "    \"lkvpnport\": \"53128\",\n" + "    \"lk_desc\": \"我是%1$s，我已阅读并充分理解开户协议条款，知晓证券市场风险，自愿在东方财富证券开户。\",\n" + "    \"QA_Api_Url\": \"https://zqsjjkmix.18.cn\",\n" + "    \"QA_volumeAdviceLevel\": \"0.5\",\n" + "    \"lkdouble_addr\": \"lkkhsrvmix.18.cn\",\n" + "    \"lkdouble_port\": \"\",\n" + "    \"lkdouble_photourl\": \"\",\n" + "    \"lkdouble_queueid\": \"06A0EBFD-BFD7-4E0C-88E5-EA8E10433C8C\"\n" + "  },\n" + "  \"option_socket_info\": {\n" + "    \"option_server_1\": \"101.231.174.68:1860,[240e:0688:0200:2500:101:231:174:68]:1860\",\n" + "    \"option_server_2\": \"109.244.41.201:80,[2402:4e00:4010:28:109:244:41:201]:80\"\n" + "  },\n" + "  \"yybdm_server_mapping\": [\n" + "    {\n" + "      \"Yybdm\": \"0000\",\n" + "      \"ServerList\": [\n" + "        {\n" + "          \"h5server-1\": \"https://mixh5tradejqdxmix.18.cn/,https://mixh5tradejqdxmix.18.cn/\",\n" + "          \"financetcpserver-1\": \"101.231.174.70:1866,[240e:0688:0200:2500:101:231:174:70]:1866\"\n" + "        },\n" + "        {\n" + "          \"h5server-2\": \"https://mixh5tradejqtxmix.18.cn/,https://mixh5tradejqtxmix.18.cn/\",\n" + "          \"financetcpserver-2\": \"109.244.41.203:1866,[2402:4e00:4010:28:109:244:41:203]:1866\"\n" + "        }\n" + "      ]\n" + "    }\n" + "  ],\n" + "  \"gm_server_mapping\": {\n" + "    \"AppKey\": {\n" + "      \"gm_coordination_appkey\": \"UsEvS3Tg\",\n" + "      \"gm_coordination_appsec\": \"dkAaX9qv\",\n" + "      \"gm_server_appkey\": \"84zv6Bgf\",\n" + "      \"gm_server_appsec\": \"rjwUVtsK\"\n" + "    },\n" + "    \"ServerList\": [\n" + "      {\n" + "        \"gm_oneway_ssl_url1\": \"101.231.174.97:10022,[240e:0688:0200:2500:101:231:174:97]:10022\",\n" + "        \"gm_twoway_ssl_url1\": \"101.231.174.97:10023,[240e:0688:0200:2500:101:231:174:97]:10023\",\n" + "        \"gm_coordinationurl1\": \"https://gmgatewayjqdx.18.cn:10020,https://gmgatewayjqdxmix.18.cn:10020\",\n" + "        \"gm_serverurl1\": \"https://gmgatewayjqdx.18.cn:10019/sdk,https://gmgatewayjqdxmix.18.cn:10019/sdk\"\n" + "      },\n" + "      {\n" + "        \"gm_oneway_ssl_url2\": \"162.14.139.136:10022,[2402:4e00:4010:28:162:14:139:136]:10022\",\n" + "        \"gm_twoway_ssl_url2\": \"162.14.139.136:10023,[2402:4e00:4010:28:162:14:139:136]:10023\",\n" + "        \"gm_coordinationurl2\": \"https://gmgatewayjqtx.18.cn:10020,https://gmgatewayjqtxmix.18.cn:10020\",\n" + "        \"gm_serverurl2\": \"https://gmgatewayjqtx.18.cn:10019/sdk,https://gmgatewayjqtxmix.18.cn:10019/sdk\"\n" + "      }\n" + "    ]\n" + "  },\n" + "  \"data_center_config\": {\n" + "    \"controladdress\": [\n" + "      \"tradenavigator1mix.18.cn:1818\",\n" + "      \"tradenavigator2mix.18.cn:1818\"\n" + "    ],\n" + "    \"downloadaddress\": [\n" + "      \"https://nhtradecfg.18.cn/multialive/\",\n" + "      \"https://txtradecfg.18.cn/multialive/\"\n" + "    ],\n" + "    \"poplist\": [\n" + "      {\n" + "        \"popid\": \"04\",\n" + "        \"popname\": \"接入点04\",\n" + "        \"field1\": \"http://180.163.42.78:7080/testping.txt\",\n" + "        \"field2\": \"万国三网\",\n" + "        \"field3\": \"http://180.163.42.78:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"05\",\n" + "        \"popname\": \"接入点05\",\n" + "        \"field1\": \"http://180.163.42.78:7080/testping.txt\",\n" + "        \"field2\": \"万国电信\",\n" + "        \"field3\": \"http://180.163.42.78:7080/testping.txt,http://[240e:96c:200:100::177]:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"06\",\n" + "        \"popname\": \"接入点06\",\n" + "        \"field1\": \"http://114.141.154.110:7080/testping.txt\",\n" + "        \"field2\": \"万国BGP\",\n" + "        \"field3\": \"http://114.141.154.110:7080/testping.txt,http://[2400:8200:0000:0005::177]:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"07\",\n" + "        \"popname\": \"接入点07\",\n" + "        \"field1\": \"http://109.244.41.244:7080/testping.txt\",\n" + "        \"field2\": \"腾讯BGP\",\n" + "        \"field3\": \"http://109.244.41.244:7080/testping.txt,http://[2400:8200:0000:0005::177]:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"09\",\n" + "        \"popname\": \"海外aws1\",\n" + "        \"field1\": \"http://18.167.177.153:8080/testping.txt\",\n" + "        \"field2\": \"海外aws1\",\n" + "        \"field3\": \"http://18.167.177.153:8080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"10\",\n" + "        \"popname\": \"接入点08\",\n" + "        \"field1\": \"http://101.231.174.36:7080/testping.txt\",\n" + "        \"field2\": \"金桥电信\",\n" + "        \"field3\": \"http://101.231.174.36:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"20\",\n" + "        \"popname\": \"接入点09\",\n" + "        \"field1\": \"http://109.244.41.213:7080/testping.txt\",\n" + "        \"field2\": \"金桥腾讯v4\",\n" + "        \"field3\": \"http://109.244.41.213:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"21\",\n" + "        \"popname\": \"接入点10\",\n" + "        \"field1\": \"http://109.244.41.213:7080/testping.txt\",\n" + "        \"field2\": \"金桥腾讯MIX\",\n" + "        \"field3\": \"http://109.244.41.213:7080/testping.txt,http://[2402:4e00:4010:28:109:244:41:213]:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"22\",\n" + "        \"popname\": \"接入点11\",\n" + "        \"field1\": \"http://101.231.174.80:7080/testping.txt\",\n" + "        \"field2\": \"金桥电信MIX\",\n" + "        \"field3\": \"http://101.231.174.80:7080/testping.txt,http://[240e:0688:0200:2500:101:231:174:80]:7080/testping.txt\"\n" + "      }\n" + "    ]\n" + "  },\n" + "  \"topItem\": [\n" + "    {\n" + "      \"title\": \"期权\",\n" + "      \"url\": \"dfcft://tradehome?tab_position=4\",\n" + "      \"event\": \"jy.nav.tab.qqjy\"\n" + "    },\n" + "    {\n" + "      \"title\": \"模拟\",\n" + "      \"url\": \"https://emrnweb.eastmoney.com/mnjy/index.html\",\n" + "      \"event\": \"jy.nav.tab.mntrade\"\n" + "    },\n" + "    {\n" + "      \"title\": \"期货\",\n" + "      \"url\": \"https://qhemrs.eastmoney.com/h5/TradeHome/index.html\",\n" + "      \"event\": \"jy.nav.tab.future\"\n" + "    }\n" + "  ]\n" + "}";


    public static final String VALUE_AFTER = "{\n" + "  \"trade_base_info\": {\n" + "    \"quoteswitch\": \"1\",\n" + "    \"telNum\": \"95357\",\n" + "    \"REQUEST_MAX_TIME\": \"300\",\n" + "    \"version\": \"1.1\",\n" + "    \"codeonlineswitchhttp\": \"0\",\n" + "    \"app_kaihu_verify_switch\": \"1\",\n" + "    \"kaihu_huoti_switch\": \"1\",\n" + "    \"kaihu_huoti_step\": \"1\",\n" + "    \"quoteswitch_hk\": \"1\",\n" + "    \"quoteswitch_usa\": \"1\",\n" + "    \"kaihu_doublevideo_switch\": \"1\",\n" + "    \"kaihu_huoti_retry_step\": \"1\",\n" + "    \"trade_nosession_switch\": \"1\",\n" + "    \"trade_nosession_device_switch\": \"0\",\n" + "    \"app_gm_switch\": \"2\",\n" + "    \"app_trade_cancel_switch\": \"1\",\n" + "    \"app_todolist_switch\": \"1\",\n" + "    \"app_loginphone_auth_switch\": \"1\",\n" + "    \"app_trade_bjsbond_switch\": \"1\",\n" + "    \"kaihu_idcard_guide_switch\": \"1\",\n" + "    \"trade_batch_tradeorder_num\": \"40\",\n" + "    \"trade_batch_cancelorder_num\": \"40\",\n" + "    \"app_home_zcwf_1_switch\": \"1\",\n" + "    \"app_home_zcwf_2_switch\": \"1\",\n" + "    \"app_chigudays_switch\": \"1\",\n" + "    \"app_kaihu_hometab_switch\": \"1\",\n" + "    \"app_trade_tradenoconfirm_switch\": \"1\",\n" + "    \"app_home_assetcache_switch\": \"1\",\n" + "    \"app_home_assetcache_time\": \"24\",\n" + "    \"app_home_zcwf_3_switch\": \"1\",\n" + "    \"app_home_zcwf_4_switch\": \"1\",\n" + "    \"app_trade_costprice_switch\": \"1\",\n" + "    \"app_trade_loginphone_switch\": \"1\",\n" + "    \"app_trade_ipv6_disable_switch\": \"0\",\n" + "    \"app_trade_home_cornermark_switch\": \"0\",\n" + "    \"app_home_faceid_fingerprint_loopday\": \"14\",\n" + "    \"app_trade_ipo_switch\": \"1\",\n" + "    \"app_trade_bzstock_mapping_switch\": \"0\",\n" + "    \"trade_batch_tradeorder_view_num\": \"999\",\n" + "    \"app_trade_viplevel_switch\": \"0\",\n" + "    \"app_trade_homeviplevel_switch\": \"1\",\n" + "    \"app_trade_summary_deal_switch\": \"1\",\n" + "    \"trade_summary_deal_num\": \"5000\",\n" + "    \"trade_record_ggt_merge_switch\": \"1\",\n" + "    \"trade_modify_price_switch\": \"1\",\n" + "    \"app_trade_ipv6_connect_timeout\": \"5\"\n" + "  },\n" + "  \"trade_url_info\": {\n" + "    \"quoteswitchurl-1\": \"https://emhsmarketwgmix.eastmoneysec.com/api/shszmisc/appquote/\",\n" + "    \"quoteswitchurl-2\": \"https://emhsmarketjqtxmix.18.cn/api/shszmisc/appquote/\",\n" + "    \"quoteswitchhkurl-1\": \"https://hkmarketzp.eastmoney.com/api/hkmisc/appquote/\",\n" + "    \"quoteswitchhkurl-2\": \"https://hkmarket.eastmoney.com/api/hkmisc/appquote/\",\n" + "    \"trade_base\": \"https://mixh5tradetxbgpmix.18.cn\",\n" + "    \"trade_base_resource\": \"https://xcwebresourcewg.securities.eastmoney.com\",\n" + "    \"trade_ad_url\": \"/Assets/GetAd_Notice?type=4&adCode=679\",\n" + "    \"trade_message_center_url\": \"https://emdcmessagemix.18.cn\",\n" + "    \"txTradeUrl\": \"https://simpletradebgp.18.cn/login/transit.html\",\n" + "    \"txTradeQuickBuyUrl\": \"https://simpletradebgp.18.cn/login/transit.html\",\n" + "    \"txTradeQuickSellUrl\": \"https://simpletradebgp.18.cn/login/transit.html\",\n" + "    \"GET_NOTICE_DATA_URL\": \"https://tradenoticemix.18.cn/api/NoticeApi/GetLayerNoticeList\",\n" + "    \"POST_NOTICE_DATA_URL\": \"https://tradenoticemix.18.cn/api/NoticeApi/SaveLayerNoticeClick\",\n" + "    \"TRADE_QRQM_ASSET_URL\": \"https://alytxbgpmix.18.cn/Analyze/GetAssets\",\n" + "    \"TRADE_QRQM_AD_URL\": \"https://emdcadvertise.eastmoney.com/infoService/v2\",\n" + "    \"trade_message_center_hint_url\": \"https://emdcmessagemix.18.cn\",\n" + "    \"TRADE_QRQM_ASSET_ANALYSIS_URL\": \"dfcft://quicktrade?tab_position=4\",\n" + "    \"trade_gateway_http_url\": \"https://tradegatewaytxbgpmix.18.cn/\",\n" + "    \"app_setting_storage_url\": \"https://emdcdataconfig.eastmoney.com/configService\",\n" + "    \"trade_filesystem_url\": \"https://filesystemmix.18.cn:8081\",\n" + "    \"rnhisquoteswitchurl-1\": \"https://push2his.eastmoney.com\",\n" + "    \"rnquoteswitchurl-1\": \"https://emhsmarketwgmix.eastmoneysec.com\",\n" + "    \"rnhkquoteswitchurl-1\": \"https://hkmarketzp.eastmoney.com\",\n" + "    \"trade_gateway_http_url_bak\": \"https://tradegatewaywgdxmix.18.cn/\",\n" + "    \"trade_hk_url\": \"https://hktrade.eastmoney.com/V65Hybrid\",\n" + "    \"trade_usa_url\": \"https://hktrade.eastmoney.com/V65Hybrid\",\n" + "    \"trade_hkApp_url\": \"https://acttg.eastmoney.com/pub/apptg_app_act_xzcjydj_01_01_01_0?_track=other.click&_trackid=2021012100002\",\n" + "    \"trade_hkAppipo_url\": \"https://acttg.eastmoney.com/pub/apptg_app_act_zbxgcjy_01_01_01_0?_track=other.click&_trackid=2021012100001\",\n" + "    \"trade_home_subtitle\": \"https://emdcadvertise.eastmoney.com/infoService/trade\",\n" + "    \"newrnquoteswitchurl-1\": \"https://emhsmarketwgmix.eastmoneysec.com\",\n" + "    \"trade_rnweb_url\": \"https://emrntradexcwgmix.18.cn\",\n" + "    \"trade_condition_url\": \"https://emdctrade.eastmoneysec.com/api/user/conditionV2\"\n" + "  },\n" + "  \"tradelogin\": {\n" + "    \"trade_czjymm\": \"https://wgopenapptxmix.18.cn/Password?from=native\",\n" + "    \"trade_webrukou\": \"https://h5tradetxbgpmix.18.cn/LogIn/Transit\",\n" + "    \"chat_on_line\": \"dfcft://emrn?id=znkf&page=Guide\",\n" + "    \"cjwt_url\": \"dfcft://emrn?id=HelpCenter\",\n" + "    \"yjsl_url\": \"dfcft://router/launcher/feedBack?feedBackId=327680\",\n" + "    \"feature_login_phone_auth\": \"0\",\n" + "    \"zhyty_url\": \"dfcft://emrn?id=Unregister&page=ActiveAccount&title=%E9%87%8D%E5%90%AF%E8%B5%84%E9%87%91%E8%B4%A6%E5%8F%B7\"\n" + "  },\n" + "  \"AStock_Account_Info\": {\n" + "    \"Account_Api_Url\": \"https://khapitxbgpmix.18.cn\",\n" + "    \"AnychatIp\": \"videoauthtxbgp.18.cn\",\n" + "    \"AnychatPort\": \"8971\",\n" + "    \"Kh_FirstStep_Config_Url\": \"\",\n" + "    \"Kh_LastStep_Config_Url\": \"\",\n" + "    \"Kaihu_h5_Url\": \"https://wgopenapptxmix.18.cn/Regist/GotoAccountResult\",\n" + "    \"video_min_time\": \"7\",\n" + "    \"video_max_time\": \"25\",\n" + "    \"kaihu_hometab_url\": \"https://marketing.dfcfs.cn/views/tradekh/index\",\n" + "    \"kaihu_hometab_skin_w\": \"#FF3F06\",\n" + "    \"kaihu_hometab_skin_b\": \"#1C1D1E\",\n" + "    \"rn_openacc_url\": \"https://wgopenapptxmix.18.cn/?start=native&ad_id=20040_2_4\",\n" + "    \"video_voice_callback_url\": \"https://zqsjjkmix.18.cn\"\n" + "  },\n" + "  \"Langke_Account_Info\": {\n" + "    \"Account_Api_Url\": \"https://khapitxbgpmix.18.cn/\",\n" + "    \"lkaddr\": \"lkkhsrvmix.18.cn\",\n" + "    \"lkport\": \"443\",\n" + "    \"lkprotocol\": \"s\",\n" + "    \"Kaihu_h5_Url\": \"https://wgopenapptxmix.18.cn/Regist/GotoAccountResult\",\n" + "    \"video_min_time\": \"7\",\n" + "    \"video_max_time\": \"30\",\n" + "    \"lklowerlimit\": \"0.3\",\n" + "    \"volumeAdviceLevel\": \"0.6\",\n" + "    \"volumeLowerLevel\": \"0.2\",\n" + "    \"tooFarFaceRatio\": \"0.4\",\n" + "    \"tooCloseFaceRatio\": \"0.85\",\n" + "    \"photoBlurValue\": \"6.0\",\n" + "    \"brightnessValue\": \"-2\",\n" + "    \"lkvpnaddr\": \"106.14.35.180\",\n" + "    \"lkvpnport\": \"53128\",\n" + "    \"lk_desc\": \"我是%1$s，我已阅读并充分理解开户协议条款，知晓证券市场风险，自愿在东方财富证券开户。\",\n" + "    \"QA_Api_Url\": \"https://zqsjjkmix.18.cn\",\n" + "    \"QA_volumeAdviceLevel\": \"0.5\",\n" + "    \"lkdouble_addr\": \"lkkhsrvmix.18.cn\",\n" + "    \"lkdouble_port\": \"\",\n" + "    \"lkdouble_photourl\": \"\",\n" + "    \"lkdouble_queueid\": \"06A0EBFD-BFD7-4E0C-88E5-EA8E10433C8C\"\n" + "  },\n" + "  \"option_socket_info\": {\n" + "    \"option_server_1\": \"109.244.41.244:1860,[2402:4e00:4010:29::42:74]:1860\",\n" + "    \"option_server_2\": \"180.163.42.74:1860,[240e:96c:200:100::42:74]:1860\"\n" + "  },\n" + "  \"yybdm_server_mapping\": [\n" + "    {\n" + "      \"Yybdm\": \"0000\",\n" + "      \"ServerList\": [\n" + "        {\n" + "          \"h5server-1\": \"https://mixh5tradetxbgpmix.18.cn/,https://mixh5tradetxbgpmix.18.cn/\",\n" + "          \"financetcpserver-1\": \"109.244.41.238:1866,[2402:4e00:4010:29::42:79]:1866\"\n" + "        },\n" + "        {\n" + "          \"h5server-2\": \"https://mixh5tradewgdxmix.18.cn/,https://mixh5tradewgdxmix.18.cn/\",\n" + "          \"financetcpserver-2\": \"180.163.42.79:1866,[240e:96c:200:100::42:79]:1866\"\n" + "        }\n" + "      ]\n" + "    }\n" + "  ],\n" + "  \"gm_server_mapping\": {\n" + "    \"AppKey\": {\n" + "      \"gm_coordination_appkey\": \"UsEvS3Tg\",\n" + "      \"gm_coordination_appsec\": \"dkAaX9qv\",\n" + "      \"gm_server_appkey\": \"84zv6Bgf\",\n" + "      \"gm_server_appsec\": \"rjwUVtsK\"\n" + "    },\n" + "    \"ServerList\": [\n" + "      {\n" + "        \"gm_oneway_ssl_url1\": \"162.14.136.133:10004,[2402:4e00:4010:29::42:168]:10004\",\n" + "        \"gm_twoway_ssl_url1\": \"162.14.136.133:10005,[2402:4e00:4010:29::42:168]:10005\",\n" + "        \"gm_coordinationurl1\": \"https://gmgatewaywgtxbgpmix.18.cn:10002,https://gmgatewaywgtxbgpmix.18.cn:10002\",\n" + "        \"gm_serverurl1\": \"https://gmgatewaywgtxbgpmix.18.cn:10001/sdk,https://gmgatewaywgtxbgpmix.18.cn:10001/sdk\"\n" + "      },\n" + "      {\n" + "        \"gm_oneway_ssl_url2\": \"180.163.42.168:10004,[240e:96c:200:100::42:168]:10004\",\n" + "        \"gm_twoway_ssl_url2\": \"180.163.42.168:10005,[240e:96c:200:100::42:168]:10005\",\n" + "        \"gm_coordinationurl2\": \"https://gmgatewaywgdx.18.cn:10002,https://gmgatewaywgdxmix.18.cn:10002\",\n" + "        \"gm_serverurl2\": \"https://gmgatewaywgdx.18.cn:10001/sdk,https://gmgatewaywgdxmix.18.cn:10001/sdk\"\n" + "      }\n" + "    ]\n" + "  },\n" + "  \"data_center_config\": {\n" + "    \"controladdress\": [\n" + "      \"tradenavigator1mix.18.cn:1818\",\n" + "      \"tradenavigator2mix.18.cn:1818\"\n" + "    ],\n" + "    \"downloadaddress\": [\n" + "      \"https://nhtradecfg.18.cn/multialive/\",\n" + "      \"https://txtradecfg.18.cn/multialive/\"\n" + "    ],\n" + "    \"poplist\": [\n" + "      {\n" + "        \"popid\": \"04\",\n" + "        \"popname\": \"接入点04\",\n" + "        \"field1\": \"http://180.163.42.78:7080/testping.txt\",\n" + "        \"field2\": \"万国三网\",\n" + "        \"field3\": \"http://180.163.42.78:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"05\",\n" + "        \"popname\": \"接入点05\",\n" + "        \"field1\": \"http://180.163.42.78:7080/testping.txt\",\n" + "        \"field2\": \"万国电信\",\n" + "        \"field3\": \"http://180.163.42.78:7080/testping.txt,http://[240e:96c:200:100::177]:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"06\",\n" + "        \"popname\": \"接入点06\",\n" + "        \"field1\": \"http://114.141.154.110:7080/testping.txt\",\n" + "        \"field2\": \"万国BGP\",\n" + "        \"field3\": \"http://114.141.154.110:7080/testping.txt,http://[2400:8200:0000:0005::177]:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"07\",\n" + "        \"popname\": \"接入点07\",\n" + "        \"field1\": \"http://109.244.41.244:7080/testping.txt\",\n" + "        \"field2\": \"腾讯BGP\",\n" + "        \"field3\": \"http://109.244.41.244:7080/testping.txt,http://[2400:8200:0000:0005::177]:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"09\",\n" + "        \"popname\": \"海外aws1\",\n" + "        \"field1\": \"http://18.167.177.153:8080/testping.txt\",\n" + "        \"field2\": \"海外aws1\",\n" + "        \"field3\": \"http://18.167.177.153:8080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"10\",\n" + "        \"popname\": \"接入点08\",\n" + "        \"field1\": \"http://101.231.174.36:7080/testping.txt\",\n" + "        \"field2\": \"金桥电信\",\n" + "        \"field3\": \"http://101.231.174.36:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"20\",\n" + "        \"popname\": \"接入点09\",\n" + "        \"field1\": \"http://109.244.41.213:7080/testping.txt\",\n" + "        \"field2\": \"金桥腾讯v4\",\n" + "        \"field3\": \"http://109.244.41.213:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"21\",\n" + "        \"popname\": \"接入点10\",\n" + "        \"field1\": \"http://109.244.41.213:7080/testping.txt\",\n" + "        \"field2\": \"金桥腾讯MIX\",\n" + "        \"field3\": \"http://109.244.41.213:7080/testping.txt,http://[2402:4e00:4010:28:109:244:41:213]:7080/testping.txt\"\n" + "      },\n" + "      {\n" + "        \"popid\": \"22\",\n" + "        \"popname\": \"接入点11\",\n" + "        \"field1\": \"http://101.231.174.80:7080/testping.txt\",\n" + "        \"field2\": \"金桥电信MIX\",\n" + "        \"field3\": \"http://101.231.174.80:7080/testping.txt,http://[240e:0688:0200:2500:101:231:174:80]:7080/testping.txt\"\n" + "      }\n" + "    ]\n" + "  },\n" + "  \"topItem\": [\n" + "    {\n" + "      \"title\": \"期权\",\n" + "      \"url\": \"dfcft://tradehome?tab_position=4\",\n" + "      \"event\": \"jy.nav.tab.qqjy\"\n" + "    },\n" + "    {\n" + "      \"title\": \"模拟\",\n" + "      \"url\": \"https://emrnweb.eastmoney.com/mnjy/index.html\",\n" + "      \"event\": \"jy.nav.tab.mntrade\"\n" + "    },\n" + "    {\n" + "      \"title\": \"期货\",\n" + "      \"url\": \"https://qhemrs.eastmoney.com/h5/TradeHome/index.html\",\n" + "      \"event\": \"jy.nav.tab.future\"\n" + "    }\n" + "  ]\n" + "}";
}
