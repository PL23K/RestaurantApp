package com.pl23k.restaurant.utils;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HelloWorld on 2017/7/18.
 */
public class Utils {

    /**
     * 路径拼接
     * @param args
     * @return
     */
    public static String CatPath(String... args){
        if(args.length == 1){
            return args[0];
        }

        StringBuilder sb = new StringBuilder();
        char sp = '\\';
        //查找分割符
        for (String arg : args) {
            if(arg.indexOf('\\') > -1){
                sp = '\\';
                break;
            }
            if(arg.indexOf('/') > -1){
                sp = '/';
                break;
            }
        }
        //拼接
        for (String arg : args) {
            sb.append(arg);
            if(arg.lastIndexOf(sp) != arg.length()-1){
                sb.append(sp);
            }
        }
        return sb.toString();
    }

    /**
     * int转换为4个字节
     * @param res
     * @return
     */
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    /**
     * 把int型先转为4个元素的byte数组，再转为字符串，每个元素之间用指定的字符串隔开
     * @param res
     * @return
     */
    public static String intToStringByByte(int res,String icon){
        String str = "";
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        str = (targets[0] & 0xFF) + str;

        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        str = (targets[1] & 0xFF) + icon + str;

        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        str = (targets[2] & 0xFF) + icon + str;

        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        str = (targets[3] & 0xFF) + icon + str;

        return str;
    }

    /**
     * 字节数组转为String类型
     * @param bytes
     * @return
     */
    public static String byteArray2String(byte[] bytes){
        String str = "";
        for (int i = bytes.length-1; i >= 0; i--) {
            int hex1 = bytes[ i ] & 0xFF ;
            str += hex1 + ".";
        }
        if (!"".equals(str)){
            str = str.substring(0,str.length()-1);
        }
        return str;
    }

	/**
	 * 颜色HSB 转 RGB
	 *
	 * @param h
	 * @param s
	 * @param v
	 * @return
	 */
	public static int[] hsb2rgb(float h, float s, float v) {
		assert Float.compare(h, 0.0f) >= 0 && Float.compare(h, 360.0f) <= 0;
		assert Float.compare(s, 0.0f) >= 0 && Float.compare(s, 1.0f) <= 0;
		assert Float.compare(v, 0.0f) >= 0 && Float.compare(v, 1.0f) <= 0;

		float r = 0, g = 0, b = 0;
		int i = (int) ((h / 60) % 6);
		float f = (h / 60) - i;
		float p = v * (1 - s);
		float q = v * (1 - f * s);
		float t = v * (1 - (1 - f) * s);
		switch (i) {
			case 0:
				r = v;
				g = t;
				b = p;
				break;
			case 1:
				r = q;
				g = v;
				b = p;
				break;
			case 2:
				r = p;
				g = v;
				b = t;
				break;
			case 3:
				r = p;
				g = q;
				b = v;
				break;
			case 4:
				r = t;
				g = p;
				b = v;
				break;
			case 5:
				r = v;
				g = p;
				b = q;
				break;
			default:
				break;
		}
		return new int[]{(int) (r * 255.0), (int) (g * 255.0),
				(int) (b * 255.0)};
	}

	/**
	 * Color对象转换成字符串
	 *
	 * @param color Color对象
	 * @return 16进制颜色字符串
	 */
	public static String toHexFromColor(Color color) {
		String r, g, b;
		StringBuilder su = new StringBuilder();
		r = Integer.toHexString(color.getRed());
		g = Integer.toHexString(color.getGreen());
		b = Integer.toHexString(color.getBlue());
		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;
		r = r.toUpperCase();
		g = g.toUpperCase();
		b = b.toUpperCase();
		su.append("#");
		su.append(r);
		su.append(g);
		su.append(b);
		//#0000FF
		return su.toString();
	}

	/**
	 * 图片缩放,w，h为缩放的目标宽度和高度
	 * src为源文件目录，dest为缩放后保存目录
	 */
	public static void zoomImage(String src,String dest,int w,int h) throws Exception {

		double wr=0,hr=0;
		File srcFile = new File(src);
		File destFile = new File(dest);

		BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
		Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

		wr=w*1.0/bufImg.getWidth();     //获取缩放比例
		hr=h*1.0 / bufImg.getHeight();

		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		Itemp = ato.filter(bufImg, null);
		try {
			ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

    /**
     * 图片缩放,固定宽高压缩
     * src为源文件目录，dest为缩放后保存目录
     */
    public static void zoomImage(String src,String dest,int size) throws Exception {

        double wr=0,hr=0;
        int w =0,h=0;
        File srcFile = new File(src);
        File destFile = new File(dest);

        BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
        if(bufImg==null || bufImg.getHeight()==0 || bufImg.getWidth()==0){
            throw new Exception("图片读取错误");
        }

        if(bufImg.getWidth() > bufImg.getHeight()){
            h = size;
            w = bufImg.getWidth()*size/bufImg.getHeight();
        }else{
            w = size;
            h = bufImg.getHeight()*size/bufImg.getWidth();
        }

        Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        try {
            ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 验证手机号是否填写正确
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (StrKit.isBlank(phone) || phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            return m.matches();
        }
    }

    /**
     * 验证邮箱是否填写正确
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        String regex = "\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}";
        if (StrKit.isBlank(email)) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(email);
            return m.matches();
        }
    }

    /**
     * 判断是否微信客户端
     * @param request
     * @return
     */
    public static boolean isWechat(HttpServletRequest request){
        //判断 是否是微信浏览器
        boolean ret = false;
        try{
            String userAgent = request.getHeader("user-agent").toLowerCase();
            if(userAgent.contains("micromessenger")){//微信客户端
                ret = true;
            }
        }catch (Exception e){
            //e.printStackTrace();
        }
        return ret;
    }

    /**
     * 取短文字
     *
     * @param src
     * @param count
     * @return
     */
    public static final String Short(String src, int count) {
        if (null == src || "".equals(src.trim())) {
            return "";
        }
        if (count <= 0) {
            return src.trim();
        }
        if (src.trim().length() > count) {
            return src.trim().substring(0, count);
        } else {
            return src.trim();
        }
    }

    /**
     * 取短文字
     *
     * @param src
     * @param count
     * @param append
     * @return
     */
    public static final String Short(String src, int count, String append) {
        if (null == src || "".equals(src.trim())) {
            return "";
        }
        if (count <= 0) {
            return src.trim();
        }
        if (src.trim().length() > count) {
            return src.trim().substring(0, count) + append;
        } else {
            return src.trim();
        }
    }

    /**
     * 生成短8位UUID
     */
    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    /**
     * 创建指定数量的随机字符串
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String createRandom(boolean numberFlag, int length){
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }

    /**
     * 保存图片
     * @param uploadFile 输入文件
     * @param filePath 保存文件目录（相对路径）
     * @param bThumbFile 是否要缩略图（为空则没有缩略图）
     * @return
     */
    public static Ret saveFile(UploadFile uploadFile, String filePath, Boolean bThumbFile){
        Ret data = new Ret();
        data.set("message","")
                .set("code",0)
                .set("status","error");
        try{
            //保存图片
            String picture;
            String pictureThumb;
            String ext = uploadFile.getFileName().substring(uploadFile.getFileName().lastIndexOf("."));
            long  time = System.currentTimeMillis();
            picture = filePath+ File.separator+"file-"+time+ext;//加随机数为了避免缓存
            pictureThumb = filePath+ File.separator+"file-"+time+"-thumb"+ext;//加随机数为了避免缓存
            picture = picture.replace("\\","/");
            picture = picture.replace("//","/");
            pictureThumb = pictureThumb.replace("\\","/");
            pictureThumb = pictureThumb.replace("//","/");
            String newPictureFullFileName = PathKit.getWebRootPath()+File.separator+picture;
            String newThumbPictureFullFileName = PathKit.getWebRootPath()+File.separator+pictureThumb;
            newPictureFullFileName = newPictureFullFileName.replace("\\","/");
            newPictureFullFileName = newPictureFullFileName.replace("//","/");
            newThumbPictureFullFileName = newThumbPictureFullFileName.replace("\\","/");
            newThumbPictureFullFileName = newThumbPictureFullFileName.replace("//","/");

            uploadFile.getFile().renameTo(new File(newPictureFullFileName));//原图
            uploadFile.getFile().delete();

            if(bThumbFile){
                Utils.zoomImage(newPictureFullFileName,newThumbPictureFullFileName,110);//创建缩放图  110px
            }

            //成功
            data.set("message","上传成功")
                    .set("picture",picture)
                    .set("thumb",pictureThumb)
                    .set("code",1)
                    .set("status","success");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
