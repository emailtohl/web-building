package com.github.emailtohl.building.common.encryption;

import static org.junit.Assert.assertEquals;

import java.security.KeyPair;

import org.junit.Test;

import com.github.emailtohl.building.common.encryption.Crypter;

public class CrypterTest {

	@Test
	public void testCryptUtil() {
		Crypter c = new Crypter();
		KeyPair k = c.createKeyPairs(1024);
		String plaintext = "高祖提剑入咸阳，炎炎红日升扶桑；光武龙兴成大统，金乌飞上天中央；哀哉献帝绍海宇，红轮西坠咸池傍！何进无谋中贵乱，凉州董卓居朝堂；王允定计诛逆党，李傕郭汜兴刀枪；四方盗贼如蚁聚，六合奸雄皆鹰扬；孙坚孙策起江左，袁绍袁术兴河梁；刘焉父子据巴蜀，刘表军旅屯荆襄；张燕张鲁霸南郑，马腾韩遂守西凉；陶谦张绣公孙瓒，各逞雄才占一方。曹操专权居相府，牢笼英俊用文武；威挟天子令诸侯，总领貌貅镇中土。楼桑玄德本皇孙，义结关张愿扶主；东西奔走恨无家，将寡兵微作羁旅；南阳三顾情何深，卧龙一见分寰宇；先取荆州后取川，霸业图王在天府；呜呼三载逝升遐，白帝托孤堪痛楚！孔明六出祁山前，愿以只手将天补；何期历数到此终，长星半夜落山坞！姜维独凭气力高，九伐中原空劬劳；钟会邓艾分兵进，汉室江山尽属曹。丕睿芳髦才及奂，司马又将天下交；受禅台前云雾起，石头城下无波涛；陈留归命与安乐，王侯公爵从根苗。纷纷世事无穷尽，天数茫茫不可逃。鼎足三分已成梦，后人凭吊空牢骚。";
		System.out.println("加密前： " + plaintext);
		String encodeStr = c.encrypt(plaintext, k.getPublic());
		System.out.println("加密后： " + encodeStr);
		String decodeStr = c.decrypt(encodeStr, k.getPrivate());
		System.out.println("解密后： " + decodeStr);
		assertEquals(plaintext, decodeStr);
	}

}
