package org.kteam.palm.common.utils.ccbface.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.intsig.ccrengine.CCREngine;
import com.intsig.idcardscan.sdk.CommonUtil;
import com.intsig.idcardscan.sdk.IDCardScanSDK;
import com.intsig.idcardscan.sdk.ISCardScanActivity;
import com.intsig.idcardscan.sdk.ResultData;
import com.tendyron.ocrlib.impl.OcrlibInterface;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by wutw on 2018/5/23 0023.
 */
public class OcrlibInterfaceImpl implements OcrlibInterface {

    static OcrlibInterface thiz = null;
    public static Context sContext;

    private String appkey;
    private static final String DIR_IMG_RESULT = Environment.getExternalStorageDirectory()+"/idcardscan/";
    public static final String BANKCARD_TRIM_DIR = Environment.getExternalStorageDirectory()+"/trimedcard.jpg";
    public static final String BANKCARD_ORG_DIR = Environment.getExternalStorageDirectory()+"/origiancard.jpg";

    private OcrlibInterfaceImpl() {
    }

    public static OcrlibInterface getInstance(Context context) {
        sContext = context;
        if(thiz == null) {
            thiz=new OcrlibInterfaceImpl();
        }

        return thiz;
    }



    @Override
    public void setDebug(boolean b) {

    }

    @Override
    public void ocrService(String s, String s1) {
        //初始化
        this.appkey=s1;


    }

    @Override
    public void startScanIDCardActivity(Intent intent, int side, int i1) {

        // 指定要临时保存的身份证图片路径

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_SHOW_CLOSE,
                true);//如果为true 需要在assets下添加cui_*的相关文件 显示是否有关闭和闪光灯按钮
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_SIDE_VALUE,
                side);
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_ORIENTATION,
                ISCardScanActivity.ORIENTATION_VERTICAL);
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_IMAGE_FOLDER,
                DIR_IMG_RESULT);
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_MATCH,  0xffaaffff);
        // 指定SDK相机模块ISCardScanActivity四边框角线条颜色，正常显示颜色
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_NORMAL, 0xffffffff);
        // 合合信息授权提供的APP_KEY
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_APP_KEY, appkey);
        // 指定SDK相机模块ISCardScanActivity提示字符串
        if(side== OcrlibInterface.REQUEST_IDCARD_SIDE_FRONT) {
            intent.putExtra(ISCardScanActivity.EXTRA_KEY_TIPS, "请将身份证正面与扫描边缘对齐\n并保持稳定和图片清晰");
        }else if(side== OcrlibInterface.REQUEST_IDCARD_SIDE_BACK){
            intent.putExtra(ISCardScanActivity.EXTRA_KEY_TIPS, "请将身份证反面与扫描边缘对齐\n并保持稳定和图片清晰");

        }else{
            intent.putExtra(ISCardScanActivity.EXTRA_KEY_TIPS, "请将身份证与扫描边缘对齐\n并保持稳定和图片清晰");

        }
        // IDCardScanSDK.OPEN_COMOLETE_CHECK
        // 表示完整性判断，IDCardScanSDK.CLOSE_COMOLETE_CHECK或其它值表示关闭完整判断
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COMPLETECARD_IMAGE,
                IDCardScanSDK.OPEN_COMOLETE_CHECK);

    }
    public static byte[] loadBitmap(String pathName) {
        Bitmap b = null;
        byte[] byteArray=null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            b = BitmapFactory.decodeFile(pathName, opts);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byteArray=baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            b = null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            b = null;
        }finally {
            if(b!=null) b.recycle();
        }
        return byteArray;
    }


    @Override
    public void ScanIDCardActivityResult(int resultCode, Intent intent) {
        switch(resultCode) {
            case RESULT_CANCELED:
                Log.d("ScanIDCardResult", "识别失败或取消，请参考返回错误码说明");
                if (intent != null) {
				/*
				 * 101 包名错误, 授权APP_KEY与绑定的APP包名不匹配； 102 appKey错误，传递的APP_KEY填写错误；
				 * 103 超过时间限制，授权的APP_KEY超出使用时间限制； 104
				 * 达到设备上限，授权的APP_KEY使用设备数量达到限制； 201 签名错误，授权的APP_KEY与绑定的APP签名不匹配；
				 * 202 其他错误，其他未知错误，比如初始化有问题； 203 服务器错误，第一次联网验证时，因服务器问题，没有验证通过；
				 * 204 网络错误，第一次联网验证时，没有网络连接，导致没有验证通过； 205
				 * 包名/签名错误，授权的APP_KEY与绑定的APP包名和签名都不匹配；
				 *
				 * EN: please read the document
				 */
                    int error_code = 0;
                    error_code = intent.getIntExtra(ISCardScanActivity.EXTRA_KEY_RESULT_ERROR_CODE,
                            0);
                    Toast.makeText(sContext, "Error >>> " + error_code + "\nMSG:" + CommonUtil.commentMsg(error_code),
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case RESULT_OK:

                // 获取身份证识别ResultData识别结果
                ResultData result = (ResultData) intent
                        .getSerializableExtra(ISCardScanActivity.EXTRA_KEY_RESULT_DATA);
                byte[] pciByte = loadBitmap(result.getTrimImagePath());

                intent.putExtra(OcrlibInterface.RESULT_IDCARD_PICTURE,
                        pciByte);
                if (result.isFront()) {


                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_NAME,
                            result.getName());
                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_SEX,
                            result.getSex());
                    byte[] avarByte = loadBitmap(result.getAvatarPath());
                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_AVATAR,
                            avarByte);
                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_NATION,
                            result.getNational());
                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_DATE,
                            result.getBirthday());
                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_ADDRESS,
                            result.getAddress());
                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_ID,
                            result.getId());

                } else {
                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_AUTHORITY,
                            result.getIssueauthority());
                    intent.putExtra(OcrlibInterface.RESULT_IDCARD_VALIDITY,
                            result.getValidity());

                }
                break;

            default:
                break;
        }


    }

    /**
     * 注意：银行卡的ISCardScanActivity和身份证的一样，需要用包名来区分，敬请谅解
     * @param intent
     */
    @Override
    public void startScanBankCardActivity(Intent intent) {
        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_SHOW_CLOSE,
                true);

        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_ORIENTATION,
                com.intsig.ccrengine.ISCardScanActivity.ORIENTATION_VERTICAL);

		/*
		 * @CN: 指定SDK相机模块ISCardScanActivity四边框角线条,检测到银行卡图片后的颜色
		 *
		 * @EN: set the Quadrilateral angle color when the camera is checking
		 * the picture.
		 */
        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_COLOR_MATCH,0xffaaffff );
		/*
		 * @CN: 指定SDK相机模块ISCardScanActivity四边框角线条颜色，正常显示颜色
		 *
		 * @EN: set the Quadrilateral angle default color
		 */
        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_COLOR_NORMAL, 0xffffffff);
		/*
		 * @CN: 指定SDK相机模块ISCardScanActivity提示字符串
		 *
		 * @EN: set the title of the user define camera
		 */
        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_TIPS,"请将银行卡正面与扫描边缘对齐，并保持设备稳定");
		/*
		 * @CN: 合合信息授权提供的APP_KEY
		 *
		 * @EN: set the appkey of intsig
		 */
        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_APP_KEY, appkey);
		/*
		 * @CN: 指定SDK相机模块是否返回银行卡卡号截图
		 *
		 * @EN: Specifies whether the SDK camera module returns the bank card
		 * number. set true or false
		 */
        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_GET_NUMBER_IMG, false);
		/*
		 * @CN: 指定SDK相机模块银行卡切边图路径
		 *
		 * @EN:Specify the SDK camera module bank khache edge graph path
		 */
        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_GET_TRIMED_IMG,
                BANKCARD_TRIM_DIR);
		/*
		 * @CN: 指定SDK相机模块银行卡原图路径
		 *
		 * @EN:Specify the SDK camera module bank card original path
		 */
        intent.putExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_GET_ORIGINAL_IMG,
                BANKCARD_ORG_DIR);

    }
    private String getBankCardType(int type) {
        switch (type) {
            case CCREngine.CCR_TYPE_CREDIT_CARD:
                return "贷记卡";
            case CCREngine.CCR_TYPE_DEBIT_CARD:
                return "借记卡";
            case CCREngine.CCR_TYPE_QUASI_CREDIT_CARD:
                return "准贷记卡";

            default:
                return "未知";
        }
    }

    @Override
    public void ScanBankCardActivityResult(int resultCode, Intent intent) {
        Log.e("resultCode",resultCode+"");


        switch(resultCode) {
            case RESULT_CANCELED:
                Log.d("ScanBankCardResult", "识别失败或取消，请参考返回错误码说明");
                if (intent != null) {
				/*
				 * 101 包名错误, 授权APP_KEY与绑定的APP包名不匹配； 102 appKey错误，传递的APP_KEY填写错误；
				 * 103 超过时间限制，授权的APP_KEY超出使用时间限制； 104
				 * 达到设备上限，授权的APP_KEY使用设备数量达到限制； 201 签名错误，授权的APP_KEY与绑定的APP签名不匹配；
				 * 202 其他错误，其他未知错误，比如初始化有问题； 203 服务器错误，第一次联网验证时，因服务器问题，没有验证通过；
				 * 204 网络错误，第一次联网验证时，没有网络连接，导致没有验证通过； 205
				 * 包名/签名错误，授权的APP_KEY与绑定的APP包名和签名都不匹配；
				 *
				 * EN: please read the document
				 */
                    int error_code = 0;
                    error_code=	 intent.getIntExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_RESULT_ERROR_CODE,
                            0);
                    Toast.makeText(sContext, "Error >>> " + error_code+"\nMSG:"+ com.intsig.ccrengine.CommonUtil.commentMsg(error_code),
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case RESULT_OK:


                com.intsig.ccrengine.CCREngine.ResultData result = (com.intsig.ccrengine.CCREngine.ResultData) intent
                        .getSerializableExtra(com.intsig.ccrengine.ISCardScanActivity.EXTRA_KEY_RESULT);
                byte[] pciByte = loadBitmap(BANKCARD_ORG_DIR);

                intent.putExtra(OcrlibInterface.RESULT_BANKCARD_BITMAP,
                        pciByte);
                intent.putExtra(OcrlibInterface.RESULT_BANKCARD_NUMBER,
                        result.getCardNumber());
                if (result.getCardInsName() != null)

                    intent.putExtra(OcrlibInterface.RESULT_BANKCARD_BANKNAME,
                            result.getCardInsName());
                if (result.getCardInsId() != null)
                    intent.putExtra(OcrlibInterface.RESULT_BANKCARD_BANKIDENTIFICATIONNUMBER,
                            result.getCardInsId());
                if (result.getCardHolderName() != null)
                    intent.putExtra(OcrlibInterface.RESULT_BANKCARD_CARDNAME,
                            result.getCardHolderName());
                intent.putExtra(OcrlibInterface.RESULT_BANKCARD_CARDTYPE,
                        getBankCardType(result.getBankCardType()));
                break;
            default:

                break;

        }
    }
}
