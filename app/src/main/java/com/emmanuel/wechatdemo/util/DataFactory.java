package com.emmanuel.wechatdemo.util;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.bean.Comment;
import com.emmanuel.wechatdemo.bean.ShuoShuo;
import com.emmanuel.wechatdemo.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 2016/8/17.
 */
public class DataFactory {

    public static final String CONTENTS[] = {
            "事实上，长期以来，甲骨文起诉谷歌的这起安全一直备受整个软件行业的关注。多年以来，整个软件行业都担心此案可能产生的恶意影响。如果甲骨文最终获胜，那么这可能会引发其它诸多与APIs相关的诉讼案件。对程序员而言，像谷歌使用Java这样，仿效另一种语言的APIs是极其普遍的事。",
            "个人博客 http://blog.csdn.net/acmnickzhang",
            "好好写代码",
            "SHI LI XIU YIBO YINGYU",
            "分享精神，开源为乐，共同进步！！！！！",
            "",
            "笨鸟先飞。。。"
    };
    public static final String PHOTO_URL[] = {
            "http://img4.imgtn.bdimg.com/it/u=306417407,3315079289&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1895407444,732122393&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1267133660,2011180104&fm=21&gp=0.jpg",
            "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1471400134&di=f2790eb5fdcdc6dedba437aa62a58940&src=http://img0.pconline.com.cn/pconline/1408/21/5313057_1408107-10_thumb.jpg",
            "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1471400134&di=32e656b2b72f8bf7ed0a1bf64eaa73de&src=http://pic.3h3.com/up/2013-7/201377991457564943.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2196598284,153353657&fm=21&gp=0.jpg"
    };
    public static final String PICTURE_URL[] = {
            "http://pic3.nipic.com/20090622/2605630_113023052_2.jpg",
            "http://img2.imgtn.bdimg.com/it/u=395920684,863299018&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=3504467926,1438117737&fm=206&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=663132532,1265075679&fm=206&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=430285025,1168446243&fm=206&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3814971924,4161415886&fm=206&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=731307697,3873909574&fm=206&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1681882397,3535453166&fm=206&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=2059708553,255963759&fm=206&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1023930876,2577057362&fm=206&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2349257573,7606495&fm=206&gp=0.jpg"
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            ""
    };

    public static Random random = new Random();

    public static List<ShuoShuo> createComment(int size){

        List<ShuoShuo>list = new ArrayList<>();
        for (int i=0; i<size; i++){
            ShuoShuo ss = new ShuoShuo();
            ss.user = createUser();
            ss.content = createContent();
            ss.picList = getPicList();
            ss.zanList = getZans();
            list.add(ss);
        }
        return list;
    }

    private static User createUser(){
        User user = new User();
        user.name = getName();
        user.photoUrl = getPhotoUrl();
        return user;
    }

    private static String getName(){
        Random random1 = new Random();
        int nameLen = random.nextInt(6) + 1;
        StringBuilder name = new StringBuilder("");
        for(int i=0; i<nameLen; i++){
            int word = random1.nextInt(26) + 1;
            char ch;
            if(i == 0)
                ch = (char)('A' + word - 1);
            else
                ch = (char)('a' + word - 1);
            name.append(ch);
        }
        return name.toString();
    }

    private static List<String> getZans(){
        int size = random.nextInt(6);
        if(size == 5)
            size = 15; //测试下数量很多的情况
        List<String>zans = new ArrayList<>();
        for(int i=0; i<size; i++){
            zans.add(getName());
        }
        return zans;
    }

    private static String getPhotoUrl(){
        int index = (int)(Math.random() * PHOTO_URL.length * 100) % PHOTO_URL.length;
        return PHOTO_URL[index];
    }

    private static List<String> getPicList(){
        random = new Random();
        int picSize = random.nextInt(10); //0 -- 9张
        List<String>listPic = new ArrayList<>();
        for(int i=0; i<picSize; i++){
            int index = random.nextInt(PICTURE_URL.length);
            listPic.add(PICTURE_URL[index]);
        }

        return listPic;
    }

    private static String createContent(){
        int index = (int)(Math.random() * CONTENTS.length * 100) % CONTENTS.length;
        return CONTENTS[index];
    }

}
