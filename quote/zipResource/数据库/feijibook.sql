/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50173
Source Host           : localhost:3306
Source Database       : feijibook

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2020-05-26 12:13:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `detail_record`
-- ----------------------------
DROP TABLE IF EXISTS `detail_record`;
CREATE TABLE `detail_record` (
  `id` char(64) NOT NULL,
  `account` varchar(32) NOT NULL,
  `year` char(10) NOT NULL,
  `month` char(10) NOT NULL,
  `day` char(10) NOT NULL,
  `week` char(10) NOT NULL COMMENT '星期几',
  `woy` char(10) NOT NULL COMMENT 'WeekOfYear,一年的第几周”2019-41“',
  `ioe` char(10) NOT NULL COMMENT 'IncomeOrExpend,是”支出“还是”收入“',
  `icon_url` int(32) NOT NULL,
  `detail_type` char(10) NOT NULL,
  `money` varchar(32) NOT NULL,
  `remark` varchar(32) NOT NULL,
  `img_url` varchar(256) DEFAULT NULL,
  `is_img_upload` bit(1) NOT NULL DEFAULT b'0' COMMENT '默认为false，因为添加记录不会上传照片',
  `video_url` varchar(128) DEFAULT NULL,
  `record_order` int(32) NOT NULL COMMENT '用于排序同一天的记录，数字越大，表示该记录是该天最后编辑过的',
  `is_video_upload` bit(1) NOT NULL DEFAULT b'0' COMMENT '默认还没有上传',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of detail_record
-- ----------------------------
INSERT INTO `detail_record` VALUES ('188cc26d-6d55-41d1-a0b2-f35fe717ffac', '111111', '2019', '12', '20', '星期五', '51', '支出', '2131230963', '购物', '10', '大宝', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('248730f9-ca0b-46f2-8076-7c066f4447c9', '111111', '2019', '12', '12', '星期四', '50', '支出', '2131230871', '书籍', '66', '英语书', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('39ba0bf1-2f23-4446-8a19-a60b91f5f89a', '111111', '2019', '12', '21', '星期六', '51', '支出', '2131230879', '餐饮', '2.5', '水', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('4d38153f-13ac-4753-93de-fd9b03b0d3f4', '111111', '2019', '12', '02', '星期一', '49', '支出', '2131230891', '通信', '300', '通信', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('5839a111-bc01-42c0-a2e9-d940fa1e6d18', '111111', '2019', '11', '22', '星期五', '47', '支出', '2131230923', '水果', '10', '苹果', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('5d613f20-4c65-4b07-ac9f-f8727bb6f7b3', '111111', '2019', '05', '21', '星期二', '21', '支出', '2131230887', '日用', '15', '日用', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('61130a2b-4b23-4d26-92c2-8590fe0a3888', '111111', '2019', '12', '18', '星期三', '51', '支出', '2131230963', '购物', '29.9', '饼干', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('77abe677-1c7f-4eea-ba13-8949dc5c1a93', '111111', '2019', '11', '20', '星期三', '47', '支出', '2131230879', '餐饮', '8', '餐饮', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('7b7261c1-0319-4356-a4cf-c1f5d45d0f50', '111111', '2019', '12', '21', '星期六', '51', '支出', '2131230983', '学习', '15', '打印', null, '', null, '1', '');
INSERT INTO `detail_record` VALUES ('8f964c6a-e3d7-4c48-9b4c-73d6e8912418', '111111', '2019', '11', '21', '星期四', '47', '支出', '2131230887', '日用', '1', '餐厅纸', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('a1cabec6-33b9-4cc4-822a-3ecb1a67ff5f', '111111', '2019', '03', '21', '星期四', '12', '支出', '2131230963', '购物', '50', '购物', null, '', null, '0', '');
INSERT INTO `detail_record` VALUES ('a70e64d9-58c3-46e8-8cbf-443e54a29511', '111111', '2019', '12', '21', '星期六', '51', '支出', '2131230879', '餐饮', '8', '餐饮', null, '', null, '5', '');
INSERT INTO `detail_record` VALUES ('aade7357-f9fd-4b00-8881-72fb5b9c28f0', '111111', '2019', '12', '21', '星期六', '51', '支出', '2131230879', '餐饮', '8', '餐饮', null, '', null, '6', '');

-- ----------------------------
-- Table structure for `record_type`
-- ----------------------------
DROP TABLE IF EXISTS `record_type`;
CREATE TABLE `record_type` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `account` varchar(32) NOT NULL,
  `ooa` varchar(16) NOT NULL COMMENT 'OptionalOrAdditive, “optional” “addible” 可选和可添加两种类型',
  `ioe` varchar(16) NOT NULL COMMENT 'IncomeOrExpend，是”支出“还是”收入“',
  `detail_type` varchar(16) NOT NULL,
  `icon_url` int(32) NOT NULL,
  `custom_type_name` varchar(16) DEFAULT '' COMMENT '自定义类型名称',
  `doc` varchar(7) NOT NULL DEFAULT '' COMMENT ' defaults custom两种(但值就是 d c)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of record_type
-- ----------------------------

-- ----------------------------
-- Table structure for `user_info`
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int(64) NOT NULL AUTO_INCREMENT,
  `account` varchar(32) NOT NULL,
  `nickname` varchar(32) DEFAULT NULL,
  `password` varchar(32) NOT NULL,
  `portrait` varchar(128) DEFAULT NULL,
  `sex` char(1) NOT NULL DEFAULT '男',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('18', '111111', null, '1', null, '男');
